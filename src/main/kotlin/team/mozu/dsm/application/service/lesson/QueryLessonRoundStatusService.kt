package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.lesson.dto.response.RoundSnapshotEntry
import team.mozu.dsm.adapter.`in`.lesson.dto.response.RoundStatusResponse
import team.mozu.dsm.application.exception.lesson.CannotLessonSSEConnectException
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonRoundStatusUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import team.mozu.dsm.application.port.out.team.RoundSnapshotPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class QueryLessonRoundStatusService(
    private val lessonFacade: LessonFacade,
    private val queryTeamPort: QueryTeamPort,
    private val roundSnapshotPort: RoundSnapshotPort,
    private val securityPort: SecurityPort
) : QueryLessonRoundStatusUseCase {

    override fun execute(lessonId: UUID): List<RoundStatusResponse> {
        val lesson = lessonFacade.findByLessonId(lessonId)
        val organ = securityPort.getCurrentOrgan()
        if (lesson.organId != organ.id) {
            throw CannotLessonSSEConnectException
        }

        val teams = queryTeamPort.findAllByLessonId(lessonId)
        val snapshots = roundSnapshotPort.findAllByLessonId(lessonId)
            .groupBy { it.teamId }

        return teams.map { team ->
            val teamSnapshots = snapshots[team.id] ?: emptyList()
            RoundStatusResponse(
                teamId = team.id!!,
                teamName = team.teamName,
                schoolName = team.schoolName,
                isInvestmentInProgress = team.isInvestmentInProgress,
                currentRound = lesson.curInvRound,
                snapshots = teamSnapshots
                    .sortedBy { it.invRound }
                    .map {
                        RoundSnapshotEntry(
                            round = it.invRound,
                            totalMoney = it.totalMoney,
                            cashMoney = it.cashMoney,
                            valuationMoney = it.valuationMoney,
                            profitNum = it.profitNum,
                            endedAt = it.endedAt
                        )
                    }
            )
        }
    }
}
