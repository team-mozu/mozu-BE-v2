package team.mozu.dsm.application.port.`in`.team

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID

interface ConnectTeamSSEUseCase {

    fun connectTeamSSE(teamId: UUID): SseEmitter
}
