package software.samios.api.auth

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val tokenProvider: TokenProvider,
    private val redisTemplate: RedisTemplate<String, Any>
) {

    /**
     * Authenticate a user via a POST request.
     * @param email The user's email address.
     * @param password The user's password in plaintext.
     * @return A response entity containing a token if the user is authenticated, or an error message if not.
     */
    @PostMapping("/login")
    fun login(@RequestParam email: String, @RequestParam password: String): ResponseEntity<out Any> {
        val user = authService.authenticate(email, password)
        return if (user != null) {
            val token = tokenProvider.generateToken(user.email)
            redisTemplate.opsForValue().set("token:$token", user) // Insert token and user into Redis
            ResponseEntity.ok(mapOf(
                "token" to token,
                "status" to "OK"
            ))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")
        }
    }

    /**
     * Log out an admin user via a GET request.
     *
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

    private fun extractToken(request: HttpServletRequest): String? {
        val cookie = request.cookies?.find { it.name == "token" }
        return cookie?.value
    }
}