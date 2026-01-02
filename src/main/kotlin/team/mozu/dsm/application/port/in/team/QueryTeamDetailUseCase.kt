package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.adapter.`in`.team.dto.response.TeamDetailResponse
import java.util.UUID

interface QueryTeamDetailUseCase {

    fun execute(lessonNum: String, teamId: UUID): TeamDetailResponse
}
