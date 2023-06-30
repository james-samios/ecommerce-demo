package software.samios.api.auth

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.bson.types.ObjectId
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import software.samios.api.store.customers.Address
import software.samios.api.store.customers.CustomerAccountRepository
import software.samios.api.user.AccountType
import software.samios.api.user.CustomerAccount
import java.util.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val customerAccountRepository: CustomerAccountRepository,
    private val tokenProvider: TokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val redisTemplate: RedisTemplate<String, Any>
) {

    /**
     * Authenticate a user via a POST request.
     * @param email The user's email address.
     * @param password The user's password in plaintext.
     * @param type The type of user to authenticate, whether it is a customer or staff member.
     * @return A response entity containing a token if the user is authenticated, or an error message if not.
     */
    @PostMapping("/login")
    fun login(
        @RequestParam email: String,
        @RequestParam password: String,
        @RequestParam type: AccountType
    ): ResponseEntity<out Any> {
        val user = if (type == AccountType.CUSTOMER) {
            authService.authenticateCustomerAccount(email, password)
        } else {
            authService.authenticateStaffAccount(email, password)
        }
        return if (user != null) {
            val token = tokenProvider.generateToken(user)
            ResponseEntity.ok(mapOf(
                "token" to token,
                "status" to "OK"
            ))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")
        }
    }

    /**
     * Log out a user via a GET request.
     */
    @GetMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {
        val token = extractToken(request)
        if (token != null) {
            redisTemplate.delete("token:$token") // Delete token from Redis
            val cookie = Cookie("token", null) // Replace existing token with null...
            cookie.maxAge = 0
            cookie.isHttpOnly = true
            cookie.secure = request.isSecure
            response.addCookie(cookie)
        }
        return ResponseEntity.ok().build()
    }

    @PostMapping("/register")
    fun register(@RequestBody registrationRequest: RegistrationRequest): ResponseEntity<Any> {
        val existingCustomer = customerAccountRepository.findByEmail(registrationRequest.email)
        if (existingCustomer != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already registered")
        }

        val hashedPassword = passwordEncoder.encode(registrationRequest.password)

        val customerAccount = CustomerAccount(
            id = ObjectId().toString(),
            firstName = registrationRequest.firstName,
            lastName = registrationRequest.lastName,
            email = registrationRequest.email,
            password = hashedPassword,
            accountActive = true,
            shippingAddress = registrationRequest.shippingAddress,
            billingAddress = registrationRequest.billingAddress,
            ipAddress = registrationRequest.ipAddress
        )

        customerAccountRepository.save(customerAccount)

        val token = tokenProvider.generateToken(customerAccount)

        return ResponseEntity.ok(mapOf(
            "token" to token,
            "status" to "OK"
        ))
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val cookie = request.cookies?.find { it.name == "token" }
        return cookie?.value
    }
}

data class RegistrationRequest(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val shippingAddress: Address? = null,
    val billingAddress: Address? = null,
    val ipAddress: String = ""
)