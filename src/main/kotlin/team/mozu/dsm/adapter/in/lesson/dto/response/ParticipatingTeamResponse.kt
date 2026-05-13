package team.mozu.dsm.adapter.`in`.lesson.dto.response

import team.mozu.dsm.domain.team.model.Team
import java.util.UUID

data class ParticipatingTeamResponse(
    val teamId: UUID,
    val teamName: String,
    val schoolName: String,
    val lessonNum: String,
    val isInvestmentInProgress: Boolean,
    val totalMoney: Long,
    val cashMoney: Long,
    val valuationMoney: Long
) {
    companion object {
        fun of(team: Team): ParticipatingTeamResponse = ParticipatingTeamResponse(
            teamId = team.id!!,
            teamName = team.teamName,
            schoolName = team.schoolName,
            lessonNum = team.lessonNum,
            isInvestmentInProgress = team.isInvestmentInProgress,
            totalMoney = team.totalMoney,
            cashMoney = team.cashMoney,
            valuationMoney = team.valuationMoney
        )
    }
}
