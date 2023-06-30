package software.samios.api.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.filter.OncePerRequestFilter
import software.samios.api.user.AccountType
import software.samios.api.user.UserDetailsImpl
import software.samios.api.utility.EnvLoader

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
class AuthConfig {

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun corsFilter(): CorsFilter {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf(EnvLoader.getEnvVariable("FRONTEND_URL"))
        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return CorsFilter(source)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }.cors { it.disable() } // This is not removing CSRF or CORS, it is just refreshing the config!
            .authorizeHttpRequests { authorize ->
                authorize.requestMatchers(AntPathRequestMatcher("/photos/**")).permitAll()
                authorize.requestMatchers(AntPathRequestMatcher("/auth/**")).permitAll()
                authorize.requestMatchers(AntPathRequestMatcher("/api/**")).permitAll()
                authorize.requestMatchers(AntPathRequestMatcher("/api/admin/**")).hasAuthority(AccountType.STAFF.name)
                authorize.requestMatchers(AntPathRequestMatcher("/api/customer/**")).hasAuthority(AccountType.CUSTOMER.name)
            }
            .addFilterBefore(AuthFilter(tokenProvider), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { UserDetailsImpl(null, listOf()) }
    }

    /**
     * Authentication Filter Implementation.
     * This filter is responsible for authenticating the user based on the token provided.
     */
    inner class AuthFilter(
        private val tokenProvider: TokenProvider
    ) : OncePerRequestFilter() {

        override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
        ) {
            val token = extractToken(request)
            if (token != null && tokenProvider.validateToken(token)) {
                val authentication = getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }
            filterChain.doFilter(request, response)
        }

        private fun extractToken(request: HttpServletRequest): String? {
            val cookie = request.cookies?.find { it.name == "token" }
            return cookie?.value
        }

        private fun getAuthentication(token: String): UsernamePasswordAuthenticationToken {
            val userDetails = tokenProvider.getUserDetails(token)
            return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        }
    }
}
