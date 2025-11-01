package team.mozu.dsm.domain.sse.model

import team.mozu.dsm.domain.annotation.Aggregate
import team.mozu.dsm.domain.sse.exception.InvalidEventStateTransitionException
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class SseEvent(
    val id: String,
    val clientId: String,
    val eventName: String,
    val data: String,
    val timestamp: LocalDateTime,
    val teamId: UUID? = null,
    val status: SendStatus = SendStatus.PENDING
) {
    companion object {
        fun create(
            clientId: String,
            eventName: String,
            data: String,
            teamId: UUID? = null
        ): SseEvent {
            return SseEvent(
                id = generateEventId(),
                clientId = clientId,
                eventName = eventName,
                data = data,
                timestamp = LocalDateTime.now(),
                teamId = teamId,
                status = SendStatus.PENDING
            )
        }

        private fun generateEventId(): String {
            return "${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(8)}"
        }
    }

    fun markAsSent(): SseEvent {
        if (status != SendStatus.PENDING) {
            throw InvalidEventStateTransitionException
        }
        return this.copy(status = SendStatus.SENT)
    }
}
