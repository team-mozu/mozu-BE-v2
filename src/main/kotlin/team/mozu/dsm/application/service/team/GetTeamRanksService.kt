package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamRankResponse
import team.mozu.dsm.application.exception.lesson.LessonItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.`in`.team.GetTeamRanksUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.UUID

@Service
class GetTeamRanksService(
    private val queryTeamPort: QueryTeamPort,
    private val queryLessonPort: QueryLessonPort,
    private val queryStockPort: QueryStockPort,
    private val queryLessonItemPort: QueryLessonItemPort
) : GetTeamRanksUseCase {

    @Transactional(readOnly = true)
    override fun get(lessonNum: String, teamId: UUID): List<TeamRankResponse> {
        val lesson = queryLessonPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException

        // lessonNum으로 모든 팀 조회 (정렬 없이)
        val allTeams = queryTeamPort.findAllByLessonId(lesson.id!!)

        // 수익 계산을 위한 차수: 현재 진행 차수(N) + 1 (/team/result와 동일)
        val profitCalculationRound = lesson.curInvRound + 1

        val teamRanks = allTeams.map { team ->
            // 각 팀의 주식 조회
            val stocks = queryStockPort.findAllByTeamId(team.id!!)
            val stockItemIds = stocks.map { it.itemId }.distinct()

            val lessonItemMap = if (stockItemIds.isNotEmpty()) {
                queryLessonItemPort.findAllByLessonIdAndItemIds(lesson.id, stockItemIds)
                    .associateBy { it.lessonItemId.itemId }
            } else {
                emptyMap()
            }

            // 주식 평가액 계산 (N+1 차수 기준, /team/result와 동일)
            val currentStockValuation = stocks.sumOf { stock ->
                val lessonItem = lessonItemMap[stock.itemId] ?: return@sumOf 0L
                val currentPrice = lessonItem.getPriceByRound(profitCalculationRound) ?: lessonItem.endPrice
                currentPrice * stock.quantity
            }

            // 실제 총 자산 = 현금 + 주식 평가액 (/team/result와 동일)
            val actualTotalMoney = team.cashMoney + currentStockValuation

            TeamRankResponse(
                id = team.id,
                teamName = team.teamName,
                schoolName = team.schoolName,
                totalMoney = actualTotalMoney, // 실시간 계산된 값
                isMyTeam = team.id == teamId
            )
        }.sortedByDescending { it.totalMoney } // 실시간 계산 후 정렬

        return teamRanks
    }
}
