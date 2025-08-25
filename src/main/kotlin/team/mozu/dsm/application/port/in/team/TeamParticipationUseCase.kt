package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.application.port.`in`.team.dto.request.TeamParticipationCommand
import team.mozu.dsm.application.port.`in`.team.dto.response.TeamToken

interface TeamParticipationUseCase {

    fun participate(request: TeamParticipationCommand): TeamToken
}
