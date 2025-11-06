package team.mozu.dsm.config

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@TestConfiguration
@Profile("test")
class TestRedisConfig {

    @Bean
    @Primary
    fun mockRedisCommands(): RedisCommands<String, String> {
        return Mockito.mock(RedisCommands::class.java) as RedisCommands<String, String>
    }

    @Bean
    @Primary
    fun mockRedisClient(): RedisClient {
        return Mockito.mock(RedisClient::class.java)
    }

    @Bean
    @Primary
    fun mockRedisConnection(): StatefulRedisConnection<String, String> {
        return Mockito.mock(StatefulRedisConnection::class.java) as StatefulRedisConnection<String, String>
    }
}
