package team.mozu.dsm.global.config.redis

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisKeyValueAdapter
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import io.lettuce.core.api.sync.RedisCommands

@Profile("!test")
@Configuration
@EnableRedisRepositories(
    enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP,
    keyspaceNotificationsConfigParameter = "" // Elasticache는 CONFIG 명령어 제한 → 우회
)
class RedisConfig(
    private val redisProperties: RedisProperties
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory =
        LettuceConnectionFactory(redisProperties.host, redisProperties.port)

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.setConnectionFactory(connectionFactory)

        // Redis key와 value에 대한 Serializer 설정
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = GenericJackson2JsonRedisSerializer()
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = GenericJackson2JsonRedisSerializer()

        return redisTemplate
    }

    /**
     * Redis 클라이언트 생성 및
     * 종료 시 shutdown() 메서드로 모든 리소스 정리
     */
    @Bean(destroyMethod = "shutdown")
    fun redisClient(redisProperties: RedisProperties): RedisClient {
        return RedisClient.create(
            "redis://${redisProperties.host}:${redisProperties.port}"
        )
    }

    /**
     * Redis 서버와의 물리적 네트워크 연결
     * 종료 시 close() 메서드로 연결 종료
     */
    @Bean(destroyMethod = "close")
    fun redisConnection(redisClient: RedisClient): StatefulRedisConnection<String, String> {
        return redisClient.connect()
    }

    /**
     * Redis 명령어를 동기적으로 실행할 수 있는 인터페이스
     * Redis Streams (XADD, XRANGE 등) 명령어 실행에 사용
     */
    @Bean
    fun redisCommands(connection: StatefulRedisConnection<String, String>): RedisCommands<String, String> {
        return connection.sync()
    }
}
