package software.samios.api.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import software.samios.api.utility.EnvLoader
import java.time.Duration


@Configuration
@EnableCaching
class RedisConfig {

    /**
     * Establishes connection to Redis.
     * Requires REDIS_HOST and REDIS_PORT to be set in .env file.
     */
    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = EnvLoader.getEnvVariable("REDIS_HOST") ?: "localhost"
        redisStandaloneConfiguration.port = EnvLoader.getEnvVariable("REDIS_PORT")?.toInt() ?: 6379

        return JedisConnectionFactory(redisStandaloneConfiguration)
    }

    /**
     * Configures RedisTemplate to use Jackson2JsonRedisSerializer for serialisation.
     * This allows our classes to be serialised to JSON and stored in Redis.
     */
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory, objectMapper: ObjectMapper): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory
        template.setDefaultSerializer(GenericJackson2JsonRedisSerializer(objectMapper))
        template.valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        return template
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
        }
    }

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        val cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer()))
            .entryTtl(Duration.ofMinutes(EnvLoader.getEnvVariable("REDIS_CACHE_TTL")?.toLong() ?: 10)) // Cache entries for specified time in .env file

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(cacheConfiguration)
            .build()
    }
}