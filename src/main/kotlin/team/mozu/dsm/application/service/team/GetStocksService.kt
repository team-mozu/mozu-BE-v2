package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.StockResponse
import team.mozu.dsm.application.exception.lesson.LessonItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.`in`.team.GetStocksUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import java.util.UUID

@Service
class GetStocksService(
    private val queryStockPort: QueryStockPort,
    private val queryLessonPort: QueryLessonPort,
    private val queryLessonItemPort: QueryLessonItemPort
) : GetStocksUseCase {

    @Transactional(readOnly = true)
    override fun getStocks(lessonNum: String, teamId: UUID): List<StockResponse> {
        val lesson = queryLessonPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException

        val validStocks = queryStockPort.findAllByTeamId(teamId)
            .filter { it.id != null }

        if (validStocks.isEmpty()) {
            return emptyList()
        }

        val lessonItemMap = queryLessonItemPort.findAllByLessonIdAndItemIds(
            lesson.id!!,
            validStocks
                .map { it.itemId }
                .distinct()
        ).associateBy { it.lessonItemId.itemId }

        val currentRound = lesson.curInvRound
        val previousRound = (currentRound - 1).coerceAtLeast(1)

        return validStocks.map { stock ->
            val lessonItem = lessonItemMap[stock.itemId]
                ?: throw LessonItemNotFoundException

            val currentPrice = lessonItem.getPriceByRound(currentRound)
                ?: lessonItem.endPrice
            val previousPrice = lessonItem.getPriceByRound(previousRound)
                ?: lessonItem.endPrice

            val currentValuation = currentPrice * stock.quantity
            val previousValuation = previousPrice * stock.quantity
            val valProfit = currentValuation - previousValuation
            val profitNum = if (previousValuation > 0) {
                (valProfit.toDouble() / previousValuation.toDouble()) * 100
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
