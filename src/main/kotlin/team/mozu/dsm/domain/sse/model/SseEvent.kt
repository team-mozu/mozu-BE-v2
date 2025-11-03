package team.mozu.dsm.domain.sse.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class SseEvent(
    var id: String? = null,
    val clientId: String,
    val eventName: String,
    val data: String,
    val timestamp: LocalDateTime,
    val teamId: UUID? = null
) {
    companion object {
        fun create(
            clientId: String,
            eventName: String,
            data: String,
            teamId: UUID? = null
        ): SseEvent {
            return SseEvent(
                clientId = clientId,
                eventName = eventName,
                data = data,
                timestamp = LocalDateTime.now(),
                teamId = teamId
            )
        }
    }
    /**
     * Redis에 저장된 후 streamId를 도메인 이벤트에 부여한다.
     */
    fun assignId(id: String) {
        this.id = id
    }
}
