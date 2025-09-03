package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.adapter.`in`.team.dto.response.TeamDetailResponse
import java.util.UUID

interface GetTeamDetailUseCase {

    fun getTeamDetail(lessonNum: String, teamId: UUID): TeamDetailResponse
}
