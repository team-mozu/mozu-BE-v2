package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.StockResponse
import team.mozu.dsm.application.exception.lesson.LessonItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.GetHoldStockUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.*

@Service
class GetHoldStockService(
    private val queryStockPort: QueryStockPort,
    private val queryLessonPort: QueryLessonPort,
    private val queryLessonItemPort: QueryLessonItemPort,
    private val queryTeamPort: QueryTeamPort
) : GetHoldStockUseCase {

    @Transactional(readOnly = true)
    override fun getHoldStock(teamId: UUID): List<StockResponse> {
        val team = queryTeamPort.findById(teamId) ?: throw TeamNotFoundException

        val lesson = queryLessonPort.findByLessonNum(team.lessonNum)
            ?: throw LessonNotFoundException

        val stocks = queryStockPort.findAllByTeamId(teamId).filter { it.id != null }
        if (stocks.isEmpty()) return emptyList()

        val lessonItemMap = queryLessonItemPort
            .findAllByLessonIdAndItemIds(lesson.id!!, stocks.map { it.itemId }.distinct())
            .associateBy { it.lessonItemId.itemId }

        val currentRound = lesson.curInvRound
        val nextRound = currentRound + 1

        return stocks.map { stock ->
            val lessonItem = lessonItemMap[stock.itemId] ?: throw LessonItemNotFoundException
            val currentPrice = lessonItem.getPriceByRound(currentRound) ?: lessonItem.currentMoney
            val nextPrice = lessonItem.getPriceByRound(nextRound) ?: lessonItem.currentMoney

            val currentValuation = currentPrice * stock.quantity
            val nextValuation = nextPrice * stock.quantity
            val valProfit = nextValuation - currentValuation
            val profitNum = if (currentValuation > 0) {
                (valProfit.toDouble() / currentValuation.toDouble()) * 100
            } else {
                0.0
            }

            StockResponse(
                id = stock.id!!,
                itemId = stock.itemId,
                itemName = stock.itemName,
                avgPurchasePrice = stock.avgPurchasePrice,
                quantity = stock.quantity,
                totalMoney = stock.buyMoney,
                nowMoney = currentPrice,
                valuationMoney = currentValuation,
                valProfit = valProfit,
                profitNum = profitNum
            )
        }
    }
}
