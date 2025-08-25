package team.mozu.dsm.application.team.port.`in`

import team.mozu.dsm.adapter.`in`.team.dto.request.TeamParticipationRequest
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamTokenResponse

interface TeamParticipationUseCase {

    fun execute(request: TeamParticipationRequest): TeamTokenResponse
}
