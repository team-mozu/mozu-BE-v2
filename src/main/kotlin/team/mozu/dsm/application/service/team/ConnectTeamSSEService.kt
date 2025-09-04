package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.`in`.sse.dto.SSEResponse
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.ConnectTeamSSEUseCase
import team.mozu.dsm.application.port.out.sse.SubscribeSsePort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.*

@Service
class ConnectTeamSSEService(
    private val subscribeSsePort: SubscribeSsePort,
    private val queryTeamPort: QueryTeamPort
) : ConnectTeamSSEUseCase {

    companion object {
        private const val CONNECTED_EVENT = "TEAM_SSE_CONNECTED"
    }

    override fun connectTeamSSE(teamId: UUID): SseEmitter {
        val team = queryTeamPort.findById(teamId) ?: throw TeamNotFoundException

        val clientId = "team:${team.id}"
        val emitter = subscribeSsePort.subscribe(clientId)

        try {
            emitter.send(
                SseEmitter.event()
                    .name(CONNECTED_EVENT)
                    .data(
                        SSEResponse(
                            CONNECTED_EVENT,
                            "id ${team.id}번의 학생 클라이언트 SSE 연결되었습니다."
                        )
                    )
            )
        } catch (e: Exception) {
            emitter.completeWithError(e)
        }

        return emitter
    }
}
