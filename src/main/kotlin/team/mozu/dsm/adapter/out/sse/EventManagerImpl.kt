package team.mozu.dsm.adapter.out.sse

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import team.mozu.dsm.application.port.out.sse.EventManager
import team.mozu.dsm.application.port.out.sse.SseConnectionManager
import team.mozu.dsm.domain.sse.model.SseEvent
import team.mozu.dsm.domain.sse.model.SseEventType
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@Component
class EventManagerImpl(
    private val sseConnectionManager: SseConnectionManager,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper
) : EventManager {

    private val logger = LoggerFactory.getLogger(EventManagerImpl::class.java)

    override fun createEvent(
        type: SseEventType,
        data: Any,
        lessonId: UUID?,
        teamId: UUID?
    ): SseEvent {
        return SseEvent(
            type = type,
            data = data,
            lessonId = lessonId,
            teamId = teamId
        )
    }

    override fun publishTeamEvent(teamId: UUID, event: SseEvent) {
        try {
            sseConnectionManager.sendToTeam(teamId, event)

            // 이벤트 히스토리 저장 (수업 ID가 있는 경우)
            event.lessonId?.let { lessonId ->
                saveEventHistory(lessonId, event)
            }

            logger.info(
                "Team event published: teamId={}, eventType={}, eventId={}",
                teamId,
                event.type,
                event.id
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to publish team event: teamId={}, eventType={}",
                teamId,
                event.type,
                e
            )
            throw e
        }
    }

    override fun publishAdminEvent(lessonId: UUID, event: SseEvent) {
        try {
            sseConnectionManager.sendToAdmin(lessonId, event)

            // 이벤트 히스토리 저장
            saveEventHistory(lessonId, event)

            logger.info(
                "Admin event published: lessonId={}, eventType={}, eventId={}",
                lessonId,
                event.type,
                event.id
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to publish admin event: lessonId={}, eventType={}",
                lessonId,
                event.type,
                e
            )
            throw e
        }
    }

    override fun publishBroadcastEvent(lessonId: UUID, event: SseEvent) {
        try {
            sseConnectionManager.sendToAllTeams(lessonId, event)

            // 이벤트 히스토리 저장
            saveEventHistory(lessonId, event)

            logger.info(
                "Broadcast event published: lessonId={}, eventType={}, eventId={}",
                lessonId,
                event.type,
                event.id
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to publish broadcast event: lessonId={}, eventType={}",
                lessonId,
                event.type,
                e
            )
            throw e
        }
    }

    override fun saveEventHistory(lessonId: UUID, event: SseEvent) {
        try {
            val historyKey = SseRedisKeys.eventHistoryKey(lessonId)
            val eventJson = objectMapper.writeValueAsString(event)

            // Redis List에 이벤트 추가 (시간순 정렬)
            redisTemplate.opsForList().leftPush(historyKey, eventJson)

            // 이벤트 히스토리 TTL 설정 (24시간)
            redisTemplate.expire(historyKey, 24, TimeUnit.HOURS)

            // 최대 1000개 이벤트만 유지
            redisTemplate.opsForList().trim(historyKey, 0, 999)

            logger.debug(
                "Event history saved: lessonId={}, eventType={}, eventId={}",
                lessonId,
                event.type,
                event.id
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to save event history: lessonId={}, eventType={}",
                lessonId,
                event.type,
                e
            )
        }
    }

    override fun getEventHistory(lessonId: UUID, since: LocalDateTime): List<SseEvent> {
        return try {
            val historyKey = SseRedisKeys.eventHistoryKey(lessonId)
            val eventJsonList = redisTemplate.opsForList().range(historyKey, 0, -1)

            eventJsonList?.mapNotNull { eventJson ->
                try {
                    val event = objectMapper.readValue(eventJson.toString(), SseEvent::class.java)
                    if (event.timestamp.isAfter(since)) event else null
                } catch (e: Exception) {
                    logger.error(
                        "Failed to deserialize event from history: eventJson={}",
                        eventJson,
                        e
                    )
                    null
                }
            }?.reversed() ?: emptyList() // 시간순 정렬 (오래된 것부터)
        } catch (e: Exception) {
            logger.error(
                "Failed to get event history: lessonId={}, since={}",
                lessonId,
                since,
                e
            )
            emptyList()
        }
    }

    override fun cleanupEventHistory(lessonId: UUID, before: LocalDateTime) {
        try {
            val historyKey = SseRedisKeys.eventHistoryKey(lessonId)
            val eventJsonList = redisTemplate.opsForList().range(historyKey, 0, -1)

            // 정리할 이벤트들 찾기
            val eventsToKeep = eventJsonList?.mapNotNull { eventJson ->
                try {
                    val event = objectMapper.readValue(eventJson.toString(), SseEvent::class.java)
                    if (event.timestamp.isAfter(before)) eventJson else null
                } catch (e: Exception) {
                    logger.error(
                        "Failed to deserialize event during cleanup: eventJson={}",
                        eventJson,
                        e
                    )
                    null
                }
            } ?: emptyList()

            // 리스트 재구성
            if (eventsToKeep.size < (eventJsonList?.size ?: 0)) {
                redisTemplate.delete(listOf(historyKey))
                eventsToKeep.forEach { eventJson ->
                    redisTemplate.opsForList().rightPush(historyKey, eventJson)
                }

                // TTL 재설정
                redisTemplate.expire(historyKey, 24, TimeUnit.HOURS)

                logger.info(
                    "Event history cleaned up: lessonId={}, kept={}, removed={}",
                    lessonId,
                    eventsToKeep.size,
                    (eventJsonList?.size ?: 0) - eventsToKeep.size
                )
            }
        } catch (e: Exception) {
            logger.error(
                "Failed to cleanup event history: lessonId={}, before={}",
                lessonId,
                before,
                e
            )
        }
    }
}
