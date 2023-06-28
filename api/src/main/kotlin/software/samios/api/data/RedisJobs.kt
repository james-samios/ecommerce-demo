package software.samios.api.data

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import software.samios.api.auth.TokenProvider
import java.util.*
import java.util.concurrent.TimeUnit

@Component
@EnableScheduling
class RedisJobs(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val tokenProvider: TokenProvider
) {

    /**
     * Validate existing Redis tokens.
     * Runs through all existing tokens stored in Redis.
     * Checks each token to see if it can be validated by being within the expiry time and having a valid signature.
     * If the token is not valid, it will be deleted from Redis.
     *
     * This method runs upon startup, and then every 2 hours. It will only run on one instance of this application by utilising Redis locks.
     * It is important to understand that with multiple nodes setup, this method can run at any given time.
     * Although that should be fine, as it is only deleting invalid tokens.
     */
    @Scheduled(fixedRate = 2 * 60 * 60 * 1000) // Run every 2 hours
    fun cleanupExpiredTokens() {
        val lockKey = "tokenCleanUpLock"
        val lockValue = UUID.randomUUID().toString()

        val lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue)
        if (lockAcquired == true) {
            try {
                redisTemplate.expire(lockKey, 1, TimeUnit.MINUTES) // Lock releases in 1 minute.
                val tokenKeys = redisTemplate.keys("token:*")

                tokenKeys.forEach { tokenKey ->
                    if (!tokenProvider.validateToken(tokenKey.replace("token:", ""))) {
                        redisTemplate.delete(tokenKey)
                    }
                }
            } finally {
                redisTemplate.delete(lockKey) // Release the lock
            }
        }
    }
}