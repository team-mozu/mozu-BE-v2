package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamRankResponse
import team.mozu.dsm.application.port.`in`.team.GetTeamRanksUseCase
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.UUID

@Service
class GetTeamRanksService(
    private val queryTeamPort: QueryTeamPort
) : GetTeamRanksUseCase {

    @Transactional(readOnly = true)
    override fun get(lessonNum: String, teamId: UUID): List<TeamRankResponse> {

        val allTeams = queryTeamPort.findAllByLessonNumOrderByTotalMoneyDesc(lessonNum)

        return allTeams
            .map { team ->
                TeamRankResponse(
                    id = team.id!!,
                    teamName = team.teamName,
                    schoolName = team.schoolName,
                    totalMoney = team.totalMoney,
                    isMyTeam = team.id == teamId
                )
            }
    }
}
