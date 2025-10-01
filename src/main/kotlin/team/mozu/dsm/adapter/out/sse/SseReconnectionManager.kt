package team.mozu.dsm.adapter.out.sse

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import team.mozu.dsm.application.port.out.sse.EventManager
import team.mozu.dsm.domain.sse.model.ReconnectionGuide
import team.mozu.dsm.domain.sse.model.SseEvent
import java.time.LocalDateTime
import java.util.UUID

/**
 * SSE 재연결 지원을 위한 매니저 클래스
 */
@Component
class SseReconnectionManager(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val eventManager: EventManager
) {

    private val logger = LoggerFactory.getLogger(SseReconnectionManager::class.java)

    companion object {
        private const val RECONNECTION_WINDOW_MINUTES = 30L // 30분 내 재연결 허용
        private const val MAX_MISSED_EVENTS = 50 // 최대 복구 가능한 이벤트 수
        private const val DEFAULT_RECONNECTION_DELAY_MS = 5000L // 5초
        private const val MAX_RECONNECTION_ATTEMPTS = 5
    }

    /**
     * 재연결 가이드를 생성합니다.
     */
    fun createReconnectionGuide(
        connectionId: String,
        teamId: UUID? = null,
        lessonId: UUID? = null,
        lastKnownEventId: String? = null
    ): ReconnectionGuide {
        return try {
            val now = LocalDateTime.now()
            val reconnectionWindow = now.minusMinutes(RECONNECTION_WINDOW_MINUTES)

            // 마지막 이벤트 시간 조회
            val lastEventTime = getLastEventTime(lessonId, lastKnownEventId)

            // 놓친 이벤트 수 계산
            val missedEventCount = if (lessonId != null && lastKnownEventId != null) {
                countMissedEvents(lessonId, lastKnownEventId, now)
            } else {
                0
            }

            // 재연결 가능 여부 판단
            val shouldReconnect = when {
                lastEventTime == null -> true // 이벤트 히스토리가 없으면 재연결 허용
                lastEventTime.isAfter(reconnectionWindow) -> true // 재연결 윈도우 내
                missedEventCount <= MAX_MISSED_EVENTS -> true // 놓친 이벤트가 복구 가능한 수준
                else -> false
            }

            // 재연결 URL 생성
            val reconnectionUrl = generateReconnectionUrl(teamId, lessonId)

            // 재연결 지연 시간 계산 (지수 백오프)
            val reconnectionAttempts = getReconnectionAttempts(connectionId)
            val recommendedDelay = calculateReconnectionDelay(reconnectionAttempts)

            val guide = ReconnectionGuide(
                connectionId = connectionId,
                shouldReconnect = shouldReconnect,
                recommendedDelay = recommendedDelay,
                maxRetryAttempts = MAX_RECONNECTION_ATTEMPTS,
                lastEventTime = lastEventTime,
                missedEventCount = missedEventCount,
                reconnectionUrl = reconnectionUrl,
                additionalInfo = mapOf(
                    "reconnectionAttempts" to reconnectionAttempts,
                    "reconnectionWindow" to RECONNECTION_WINDOW_MINUTES,
                    "maxMissedEvents" to MAX_MISSED_EVENTS
                )
            )

            // 재연결 가이드를 Redis에 저장 (5분 TTL)
            redisTemplate.opsForValue().set(
                SseRedisKeys.reconnectionGuideKey(connectionId),
                guide,
                java.time.Duration.ofMinutes(5)
            )

            logger.info(
                "Reconnection guide created: connectionId={}, shouldReconnect={}, missedEvents={}",
                connectionId,
                shouldReconnect,
                missedEventCount
            )

            guide
        } catch (e: Exception) {
            logger.error("Failed to create reconnection guide: connectionId={}", connectionId, e)

            // 오류 시 기본 재연결 가이드 반환
            ReconnectionGuide(
                connectionId = connectionId,
                shouldReconnect = true,
                recommendedDelay = DEFAULT_RECONNECTION_DELAY_MS,
                maxRetryAttempts = MAX_RECONNECTION_ATTEMPTS,
                lastEventTime = null,
                missedEventCount = 0,
                reconnectionUrl = generateReconnectionUrl(teamId, lessonId)
            )
        }
    }

    /**
     * 재연결 시 놓친 이벤트들을 복구합니다.
     */
    fun recoverMissedEvents(
        connectionId: String,
        lessonId: UUID,
        lastKnownEventId: String?
    ): List<SseEvent> {
        return try {
            if (lastKnownEventId == null) {
                logger.debug("No last known event ID provided, skipping event recovery: connectionId={}", connectionId)
                return emptyList()
            }

            val now = LocalDateTime.now()
            val recoveryWindow = now.minusMinutes(RECONNECTION_WINDOW_MINUTES)

            // 이벤트 히스토리에서 놓친 이벤트들 조회
            val allEvents = eventManager.getEventHistory(lessonId, recoveryWindow)

            // 마지막 알려진 이벤트 이후의 이벤트들만 필터링
            val missedEvents = filterEventsAfter(allEvents, lastKnownEventId)
                .take(MAX_MISSED_EVENTS) // 최대 복구 가능한 이벤트 수로 제한

            // 중복 이벤트 방지를 위한 처리된 이벤트 ID 기록
            recordProcessedEvents(connectionId, missedEvents.map { it.id })

            logger.info(
                "Recovered {} missed events for reconnection: connectionId={}, lessonId={}",
                missedEvents.size,
                connectionId,
                lessonId
            )

            missedEvents
        } catch (e: Exception) {
            logger.error("Failed to recover missed events: connectionId={}, lessonId={}", connectionId, lessonId, e)
            emptyList()
        }
    }

    /**
     * 재연결 시도 횟수를 증가시킵니다.
     */
    fun incrementReconnectionAttempts(connectionId: String): Int {
        return try {
            val key = SseRedisKeys.reconnectionAttemptsKey(connectionId)
            val attempts = redisTemplate.opsForValue().increment(key) ?: 1L

            // 24시간 TTL 설정
            redisTemplate.expire(key, java.time.Duration.ofHours(24))

            logger.debug("Incremented reconnection attempts: connectionId={}, attempts={}", connectionId, attempts)
            attempts.toInt()
        } catch (e: Exception) {
            logger.error("Failed to increment reconnection attempts: connectionId={}", connectionId, e)
            1
        }
    }

    /**
     * 재연결 성공 시 시도 횟수를 리셋합니다.
     */
    fun resetReconnectionAttempts(connectionId: String) {
        try {
            redisTemplate.delete(SseRedisKeys.reconnectionAttemptsKey(connectionId))
            logger.debug("Reset reconnection attempts: connectionId={}", connectionId)
        } catch (e: Exception) {
            logger.error("Failed to reset reconnection attempts: connectionId={}", connectionId, e)
        }
    }

    /**
     * 연결 상태 피드백을 생성합니다.
     */
    fun createConnectionStatusFeedback(
        connectionId: String,
        isHealthy: Boolean,
        lastActivity: LocalDateTime?
    ): Map<String, Any> {
        return try {
            val now = LocalDateTime.now()
            val guide = redisTemplate.opsForValue().get(
                SseRedisKeys.reconnectionGuideKey(connectionId)
            ) as? ReconnectionGuide

            mapOf(
                "connectionId" to connectionId,
                "isHealthy" to isHealthy,
                "lastActivity" to (lastActivity?.toString() ?: "unknown"),
                "currentTime" to now.toString(),
                "reconnectionAvailable" to (guide?.shouldReconnect ?: true),
                "recommendedDelay" to (guide?.recommendedDelay ?: DEFAULT_RECONNECTION_DELAY_MS),
                "status" to if (isHealthy) "connected" else "disconnected"
            )
        } catch (e: Exception) {
            logger.error("Failed to create connection status feedback: connectionId={}", connectionId, e)
            mapOf(
                "connectionId" to connectionId,
                "isHealthy" to isHealthy,
                "status" to "error",
                "error" to (e.message ?: "Unknown error")
            )
        }
    }

    /**
     * 중복 이벤트 방지를 위해 이벤트가 이미 처리되었는지 확인합니다.
     */
    fun isEventAlreadyProcessed(connectionId: String, eventId: String): Boolean {
        return try {
            val processedEvents = redisTemplate.opsForSet().members(
                SseRedisKeys.processedEventsKey(connectionId)
            ) as? Set<String> ?: emptySet()

            processedEvents.contains(eventId)
        } catch (e: Exception) {
            logger.error(
                "Failed to check if event is already processed: connectionId={}, eventId={}",
                connectionId,
                eventId,
                e
            )
            false
        }
    }

    private fun getLastEventTime(lessonId: UUID?, lastKnownEventId: String?): LocalDateTime? {
        return try {
            if (lessonId == null || lastKnownEventId == null) return null

            val recentEvents = eventManager.getEventHistory(
                lessonId,
                LocalDateTime.now().minusHours(1)
            )

            recentEvents.find { it.id == lastKnownEventId }?.timestamp
        } catch (e: Exception) {
            logger.error("Failed to get last event time: lessonId={}, eventId={}", lessonId, lastKnownEventId, e)
            null
        }
    }

    private fun countMissedEvents(lessonId: UUID, lastKnownEventId: String, since: LocalDateTime): Int {
        return try {
            val events = eventManager.getEventHistory(lessonId, since.minusMinutes(RECONNECTION_WINDOW_MINUTES))
            val missedEvents = filterEventsAfter(events, lastKnownEventId)
            missedEvents.size
        } catch (e: Exception) {
            logger.error("Failed to count missed events: lessonId={}, lastEventId={}", lessonId, lastKnownEventId, e)
            0
        }
    }

    private fun filterEventsAfter(events: List<SseEvent>, lastKnownEventId: String): List<SseEvent> {
        val lastEventIndex = events.indexOfFirst { it.id == lastKnownEventId }
        return if (lastEventIndex >= 0 && lastEventIndex < events.size - 1) {
            events.subList(lastEventIndex + 1, events.size)
        } else {
            events // 마지막 이벤트를 찾을 수 없으면 모든 이벤트 반환
        }
    }

    private fun generateReconnectionUrl(teamId: UUID?, lessonId: UUID?): String? {
        return when {
            teamId != null -> "/team/sse?teamId=$teamId"
            lessonId != null -> "/admin/sse?lessonId=$lessonId"
            else -> null
        }
    }

    private fun getReconnectionAttempts(connectionId: String): Int {
        return try {
            val attempts = redisTemplate.opsForValue().get(
                SseRedisKeys.reconnectionAttemptsKey(connectionId)
            ) as? Long ?: 0L
            attempts.toInt()
        } catch (e: Exception) {
            logger.error("Failed to get reconnection attempts: connectionId={}", connectionId, e)
            0
        }
    }

    private fun calculateReconnectionDelay(attempts: Int): Long {
        // 지수 백오프: 5초, 10초, 20초, 40초, 60초 (최대)
        val baseDelay = DEFAULT_RECONNECTION_DELAY_MS
        val exponentialDelay = baseDelay * (1L shl minOf(attempts, 4))
        return minOf(exponentialDelay, 60000L) // 최대 1분
    }

    private fun recordProcessedEvents(connectionId: String, eventIds: List<String>) {
        try {
            if (eventIds.isEmpty()) return

            val key = SseRedisKeys.processedEventsKey(connectionId)
            eventIds.forEach { eventId ->
                redisTemplate.opsForSet().add(key, eventId)
            }

            // 1시간 TTL 설정
            redisTemplate.expire(key, java.time.Duration.ofHours(1))

            logger.debug("Recorded {} processed events: connectionId={}", eventIds.size, connectionId)
        } catch (e: Exception) {
            logger.error("Failed to record processed events: connectionId={}", connectionId, e)
        }
    }
}
