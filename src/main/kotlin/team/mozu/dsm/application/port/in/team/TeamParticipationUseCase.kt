package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.adapter.`in`.team.dto.request.TeamParticipationRequest
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamTokenResponse

interface TeamParticipationUseCase {

    fun execute(request: TeamParticipationRequest): TeamTokenResponse
}
