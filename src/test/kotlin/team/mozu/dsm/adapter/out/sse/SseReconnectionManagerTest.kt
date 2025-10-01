package team.mozu.dsm.adapter.out.sse

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import team.mozu.dsm.application.port.out.sse.EventManager
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class SseReconnectionManagerTest {

    @Mock
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    @Mock
    private lateinit var eventManager: EventManager

    @Mock
    private lateinit var valueOperations: ValueOperations<String, Any>

    @InjectMocks
    private lateinit var reconnectionManager: SseReconnectionManager

    @Test
    fun `재연결 가이드 생성 시 기본값이 올바르게 설정되어야 한다`() {
        // Given
        val connectionId = "test-connection-id"
        val teamId = UUID.randomUUID()
        val lessonId = UUID.randomUUID()

        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)

        // When
        val guide = reconnectionManager.createReconnectionGuide(
            connectionId = connectionId,
            teamId = teamId,
            lessonId = lessonId
        )

        // Then
        assertEquals(connectionId, guide.connectionId)
        assertTrue(guide.shouldReconnect)
        assertEquals(5000L, guide.recommendedDelay)
        assertEquals(5, guide.maxRetryAttempts)
        assertEquals("/team/sse?teamId=$teamId", guide.reconnectionUrl)
    }

    @Test
    fun `재연결 시도 횟수가 증가해야 한다`() {
        // Given
        val connectionId = "test-connection-id"

        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)
        `when`(valueOperations.increment("sse:reconnection:attempts:$connectionId")).thenReturn(1L)

        // When
        val attempts = reconnectionManager.incrementReconnectionAttempts(connectionId)

        // Then
        assertEquals(1, attempts)
    }

    @Test
    fun `연결 상태 피드백이 올바르게 생성되어야 한다`() {
        // Given
        val connectionId = "test-connection-id"
        val isHealthy = true
        val lastActivity = LocalDateTime.now().minusMinutes(5)

        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)

        // When
        val feedback = reconnectionManager.createConnectionStatusFeedback(
            connectionId = connectionId,
            isHealthy = isHealthy,
            lastActivity = lastActivity
        )

        // Then
        assertEquals(connectionId, feedback["connectionId"])
        assertEquals(isHealthy, feedback["isHealthy"])
        assertEquals("connected", feedback["status"])
        assertEquals(lastActivity.toString(), feedback["lastActivity"])
    }

    @Test
    fun `이벤트가 이미 처리되었는지 확인할 수 있어야 한다`() {
        // Given
        val connectionId = "test-connection-id"
        val eventId = "test-event-id"
        val processedEvents = setOf(eventId)

        val setOperations = org.mockito.Mockito.mock(org.springframework.data.redis.core.SetOperations::class.java) as org.springframework.data.redis.core.SetOperations<String, Any>
        `when`(redisTemplate.opsForSet()).thenReturn(setOperations)
        `when`(setOperations.members("sse:processed:events:$connectionId")).thenReturn(processedEvents)

        // When
        val isProcessed = reconnectionManager.isEventAlreadyProcessed(connectionId, eventId)

        // Then
        assertTrue(isProcessed)
    }
}
