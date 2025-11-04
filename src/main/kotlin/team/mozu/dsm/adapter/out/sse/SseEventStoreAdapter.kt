package team.mozu.dsm.adapter.out.sse

import io.lettuce.core.Range
import io.lettuce.core.StreamMessage
import io.lettuce.core.api.sync.RedisCommands
import org.springframework.stereotype.Component
import team.mozu.dsm.application.port.out.sse.SseEventStorePort
import team.mozu.dsm.domain.sse.model.SseEvent
import team.mozu.dsm.global.exception.sse.SseClientIdNotFoundException
import team.mozu.dsm.global.exception.sse.SseTimestampNotFoundException
import java.time.LocalDateTime
import java.util.UUID

@Component
class SseEventStoreAdapter(
    private val redisCommands: RedisCommands<String, String>
) : SseEventStorePort {

    companion object {
        private const val CLIENT_STREAM_PREFIX = "sse:client:"
        private const val TEAM_STREAM_PREFIX = "sse:team:"
        private const val UNKNOWN_EVENT_NAME = "unknown"
    }

    override fun save(event: SseEvent) {
        val streamKey = event.teamId?.let { "$TEAM_STREAM_PREFIX$it" }
            ?: "$CLIENT_STREAM_PREFIX${event.clientId}"

        val streamData = mutableMapOf(
            "id" to event.id,
            "clientId" to event.clientId,
            "eventName" to event.eventName,
            "data" to event.data,
            "timestamp" to event.timestamp.toString()
        )

        event.teamId?.let {
            streamData["teamId"] = it.toString()
        }

        redisCommands.xadd(streamKey, streamData)

        trimOldEvents(streamKey)
    }

    override fun findEventsAfter(clientId: String, lastEventId: String?): List<SseEvent> {
        val streamKey = "$CLIENT_STREAM_PREFIX$clientId"
        return readLatestEvents(streamKey, lastEventId)
    }

    override fun findTeamEventsAfter(teamId: UUID, lastEventId: String?): List<SseEvent> {
        val streamKey = "$TEAM_STREAM_PREFIX$teamId"
        return readLatestEvents(streamKey, lastEventId)
    }

    private fun readLatestEvents(streamKey: String, lastEventId: String?): List<SseEvent> {
        val startId = lastEventId?.let { "($it" } ?: "0"
        val messages: List<StreamMessage<String, String>> =
            redisCommands.xrange(streamKey, Range.create(startId, "+")) ?: emptyList()

        val events = mutableListOf<SseEvent>()

        messages.forEach { msg ->
            val body = msg.body
            val id = body["id"] ?: msg.id
            val clientId = body["clientId"] ?: throw SseClientIdNotFoundException
            val timestamp = body["timestamp"]?.let { LocalDateTime.parse(it) } ?: throw SseTimestampNotFoundException
            val eventName = body["eventName"] ?: UNKNOWN_EVENT_NAME
            val data = body["data"] ?: ""
            val teamId = body["teamId"]?.takeIf { it.isNotBlank() }?.let(UUID::fromString)

            events.add(
                SseEvent(
                    id = id,
                    clientId = clientId,
                    eventName = eventName,
                    data = data,
                    timestamp = timestamp,
                    teamId = teamId
                )
            )
        }

        return events
    }

    /**
     * redis streams를 적용하여 시간 기반 삭제 기능이 아닌
     * 길이 기반 삭제(XTRIM)을 적용
     */
    private fun trimOldEvents(streamKey: String, maxLen: Long = 1000) {
        redisCommands.xtrim(streamKey, true, maxLen)
    }
}
