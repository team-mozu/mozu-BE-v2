package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.adapter.`in`.team.dto.response.TeamRankResponse
import java.util.UUID

interface GetTeamRanksUseCase {

    fun get(lessonNum: String, teamId: UUID): List<TeamRankResponse>
}
