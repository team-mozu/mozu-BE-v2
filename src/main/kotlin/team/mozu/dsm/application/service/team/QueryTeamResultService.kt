package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamResultResponse
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.QueryTeamResultUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.QueryOrderItemPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.UUID

@Service
class QueryTeamResultService(
    private val queryLessonPort: QueryLessonPort,
    private val queryTeamPort: QueryTeamPort,
    private val queryStockPort: QueryStockPort,
    private val queryLessonItemPort: QueryLessonItemPort,
    private val queryOrderItemPort: QueryOrderItemPort
) : QueryTeamResultUseCase {

    @Transactional(readOnly = true)
    override fun execute(lessonNum: String, teamId: UUID): TeamResultResponse {
        val lesson = queryLessonPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException

        val team = queryTeamPort.findById(teamId)
            ?: throw TeamNotFoundException

        val stocks = queryStockPort.findAllByTeamId(teamId)
        val stockItemIds = stocks.map { it.itemId }.distinct()

        val lessonItemMap = if (stockItemIds.isNotEmpty()) {
            queryLessonItemPort.findAllByLessonIdAndItemIds(lesson.id!!, stockItemIds)
                .associateBy { it.lessonItemId.itemId }
        } else {
            emptyMap()
        }

        // 투자중인 금액 (매입한 주식 총액)
        val totalBuyMoney = stocks.sumOf { it.buyMoney }

        // 수익 계산을 위한 차수: 현재 진행 차수(N) + 1
        val profitCalculationRound = lesson.curInvRound + 1

        // 주식 평가액 계산 (N+1 차수 기준)
        val currentStockValuation = stocks.sumOf { stock ->
            val lessonItem = lessonItemMap[stock.itemId] ?: return@sumOf 0L
            val currentPrice = lessonItem.getPriceByRound(profitCalculationRound) ?: lessonItem.endPrice
            currentPrice * stock.quantity
        }

        // 주문 수량 조회
        val orderCount = queryOrderItemPort.countOrderItemsByTeamId(teamId)

        return TeamResultResponse.of(team, lesson, currentStockValuation, totalBuyMoney, orderCount)
    }
}
