package team.mozu.dsm.application.port.out.sse

import team.mozu.dsm.domain.sse.model.SseEvent
import java.util.UUID

interface SseEventStorePort {

    fun save(event: SseEvent)

    fun findEventsAfter(clientId: String, lastEventId: String?): List<SseEvent>

    fun findTeamEventsAfter(teamId: UUID, lastEventId: String?): List<SseEvent>
}
