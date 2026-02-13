package team.mozu.dsm.adapter.`in`.team.dto.response

import team.mozu.dsm.domain.team.model.Team
import java.util.UUID

data class TeamRankResponse(
    val id: UUID,
    val teamName: String?,
    val schoolName: String,
    val totalMoney: Long,
    val isMyTeam: Boolean
) {
    companion object {
        fun of(team: Team, totalMoney: Long, myTeamId: UUID): TeamRankResponse {
            return TeamRankResponse(
                id = team.id!!,
                teamName = team.teamName,
                schoolName = team.schoolName,
                totalMoney = totalMoney,
                isMyTeam = team.id == myTeamId
            )
        }
    }
}
