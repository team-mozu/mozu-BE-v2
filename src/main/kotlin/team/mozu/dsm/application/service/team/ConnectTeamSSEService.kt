package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.`in`.sse.dto.SSEResponse
import team.mozu.dsm.adapter.out.sse.SsePersistenceAdapter
import team.mozu.dsm.adapter.out.sse.repository.SseEmitterRepository
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.ConnectTeamSSEUseCase
import team.mozu.dsm.application.port.out.sse.SubscribeSsePort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.UUID

@Service
class ConnectTeamSSEService(
    private val subscribeSsePort: SubscribeSsePort,
    private val queryTeamPort: QueryTeamPort,
    private val sseEmitterRepository: SseEmitterRepository,
    private val ssePersistenceAdapter: SsePersistenceAdapter
) : ConnectTeamSSEUseCase {

    companion object {
        private const val CONNECTED_EVENT = "TEAM_SSE_CONNECTED"
    }

    override fun connectTeamSSE(teamId: UUID, lastEventId: String?): SseEmitter {
        val team = queryTeamPort.findById(teamId) ?: throw TeamNotFoundException

        val clientId = "team-sse:${team.id}"

        // Use recovery-enabled subscription if lastEventId is provided
        val emitter = if (lastEventId != null) {
            ssePersistenceAdapter.subscribeWithRecovery(clientId, lastEventId, teamId)
        } else {
            subscribeSsePort.subscribe(clientId)
        }

        try {
            emitter.send(
                SseEmitter.event()
                    .name(CONNECTED_EVENT)
                    .data(
                        SSEResponse(
                            CONNECTED_EVENT,
                            "id ${team.id}번의 학생 클라이언트 SSE 연결되었습니다." + if (lastEventId != null) " (복구됨: $lastEventId)" else ""
                        )
                    )
            )
        } catch (e: Exception) {
            sseEmitterRepository.delete(clientId)
            emitter.completeWithError(e)
            throw e
        }

        return emitter
    }
}
