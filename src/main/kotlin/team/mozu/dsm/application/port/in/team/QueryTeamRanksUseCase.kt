package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.adapter.`in`.team.dto.response.TeamRankResponse
import java.util.UUID

interface QueryTeamRanksUseCase {

    fun execute(lessonNum: String, teamId: UUID): List<TeamRankResponse>
}
