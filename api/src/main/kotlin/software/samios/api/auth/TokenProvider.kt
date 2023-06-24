package software.samios.api.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import software.samios.api.admin.staff.UserDetailsImpl
import software.samios.api.admin.staff.StaffAccountRepository
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Provides methods for generating and validating JWT tokens.
 * This is currently limited to Staff Accounts but will be expanded to include customer accounts.
 */
@Service
class TokenProvider(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val staffAccountRepository: StaffAccountRepository
) {

    private val secretKeyRedisKey = "jwt:secret_key"

    private fun getSecretKey(): String {
        var secretKey = redisTemplate.opsForValue().get(secretKeyRedisKey) as? String
        if (secretKey == null) {
            secretKey = generateRandomKey()
            redisTemplate.opsForValue().set(secretKeyRedisKey, secretKey)
        }
        return secretKey
    }

    private fun generateRandomKey(): String {
        val keySize = 64
        val random = SecureRandom()
        val bytes = ByteArray(keySize)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    fun generateToken(email: String): String {
        val secretKey = getSecretKey()
        val expirationDate = Date(Date().time + TimeUnit.HOURS.toMillis(12))

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date())
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = extractClaims(token)
            !claims.expiration.before(Date())
        } catch (ex: Exception) {
            false
        }
    }

    fun getUserDetails(token: String): UserDetails {
        val claims = extractClaims(token)
        val email = claims.subject
        val user = staffAccountRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found")
        return UserDetailsImpl(user)
    }

    private fun extractClaims(token: String): Claims {
        val secretKey = redisTemplate.opsForValue().get(secretKeyRedisKey) as String
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .body
    }
}
