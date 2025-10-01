package team.mozu.dsm.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.RedisTemplate

@TestConfiguration
class TestRedisConfig {

    @Bean
    @Primary
    fun testRedisTemplate(): RedisTemplate<String, Any> {
        return Mockito.mock(RedisTemplate::class.java) as RedisTemplate<String, Any>
    }

    @Bean
    @Primary
    fun testObjectMapper(): ObjectMapper {
        return ObjectMapper()
    }
}
