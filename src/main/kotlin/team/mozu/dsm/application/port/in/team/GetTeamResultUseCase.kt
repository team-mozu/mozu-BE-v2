package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.adapter.`in`.team.dto.response.TeamResultResponse
import java.util.UUID

interface GetTeamResultUseCase {

    fun execute(lessonNum: String, teamId: UUID): TeamResultResponse
}
