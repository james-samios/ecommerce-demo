package software.samios.api.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import software.samios.api.user.UserDetailsImpl
import software.samios.api.admin.staff.StaffAccountRepository
import software.samios.api.store.customers.CustomerAccountRepository
import software.samios.api.user.AccountType
import software.samios.api.user.UserAccount
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
    private val staffAccountRepository: StaffAccountRepository,
    private val customerAccountRepository: CustomerAccountRepository
) {

    private val secretKeyRedisKey = "jwt:secretKey"

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

    fun generateToken(account: UserAccount): String {
        val secretKey = getSecretKey()
        val expirationDate = Date(Date().time + TimeUnit.HOURS.toMillis(12))

        val roles = mutableListOf<String>()

        // We have to do this first for the custom SpEL expression to work
        if (account.accountType == AccountType.STAFF) {
            val staffAccount = staffAccountRepository.findByEmail(account.userEmail)
            if (staffAccount != null) {
                roles.add(staffAccount.access.name)
            }
        }

        roles.add(account.accountType.name)

        val token = Jwts.builder()
            .setSubject(account.userEmail)
            .claim("roles", roles)
            .setIssuedAt(Date())
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()

        redisTemplate.opsForValue().set("token:$token", account) // Insert token and user into Redis
        return token
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = extractClaims(token)
            !claims.expiration.before(Date())
        } catch (ex: Exception) {
            false
        }
    }

    private fun getTokenType(token: String, accountType: AccountType): Boolean {
        return try {
            val claims = extractClaims(token)
            val roles = claims["roles"] as List<*>
            return roles.contains(accountType.name)
        } catch (ex: Exception) {
            false
        }
    }

    fun getUserDetails(token: String): UserDetails {
        val claims = extractClaims(token)
        val email = claims.subject
        val user = if (getTokenType(token, AccountType.STAFF)) {
            staffAccountRepository.findByEmail(email)
        } else {
            customerAccountRepository.findByEmail(email)
        }
        if (user == null) throw UsernameNotFoundException("User not found") // todo: delete token here?
        val roles = claims["roles"] as List<*>
        val authorities = roles.map { GrantedAuthority { it.toString() } }

        return UserDetailsImpl(user, authorities)
    }

    private fun extractClaims(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(getSecretKey())
            .parseClaimsJws(token)
            .body
    }
}
