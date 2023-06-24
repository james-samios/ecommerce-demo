package software.samios.api.data

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import software.samios.api.utility.EnvLoader

@Configuration
class RedisConfig {

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = EnvLoader.getEnvVariable("REDIS_HOST") ?: "localhost"
        redisStandaloneConfiguration.port = EnvLoader.getEnvVariable("REDIS_PORT")?.toInt() ?: 6379

        return JedisConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    fun redisTemplate(jedisConnectionFactory: JedisConnectionFactory, objectMapper: ObjectMapper): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = jedisConnectionFactory

        val stringRedisSerializer = StringRedisSerializer()
        val genericJackson2JsonRedisSerializer = GenericJackson2JsonRedisSerializer(objectMapper)

        redisTemplate.keySerializer = stringRedisSerializer
        redisTemplate.valueSerializer = genericJackson2JsonRedisSerializer
        redisTemplate.hashKeySerializer = stringRedisSerializer
        redisTemplate.hashValueSerializer = genericJackson2JsonRedisSerializer

        return redisTemplate
    }
}