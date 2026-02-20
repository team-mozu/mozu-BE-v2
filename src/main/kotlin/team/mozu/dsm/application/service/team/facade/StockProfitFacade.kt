package team.mozu.dsm.application.service.team.facade

import org.springframework.stereotype.Component
import team.mozu.dsm.application.exception.lesson.LessonItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.team.CommandStockPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.domain.lesson.model.Lesson
import java.time.LocalDateTime
import java.util.UUID

/**
 * 이번 차수에 거래하지 않은 보유 종목의 평가손익과 수익률을 현재가 기준으로 업데이트
 */
@Component
class StockProfitFacade(
    private val queryStockPort: QueryStockPort,
    private val queryLessonItemPort: QueryLessonItemPort,
    private val commandStockPort: CommandStockPort
) {
    fun updateNonTradedStocksProfit(teamId: UUID, lesson: Lesson, tradedItemIds: List<Int>) {
        val lessonId = lesson.id ?: throw LessonNotFoundException
        val currentRound = lesson.curInvRound

        val nonTradedStocks = queryStockPort.findAllByTeamId(teamId)
            .filter { it.itemId !in tradedItemIds }

        if (nonTradedStocks.isEmpty()) return

        val lessonItemMap = queryLessonItemPort
            .findAllByLessonIdAndItemIds(lessonId, nonTradedStocks.map { it.itemId }.distinct())
            .associateBy { it.lessonItemId.itemId }

        val stocksToUpdate = nonTradedStocks.map { stock ->
            val lessonItem = lessonItemMap[stock.itemId] ?: throw LessonItemNotFoundException
            val currentPrice = lessonItem.getPriceByRound(currentRound) ?: lessonItem.endPrice

            val currentValProfit = (currentPrice * stock.quantity) - stock.buyMoney
            val currentProfitNum = if (stock.buyMoney > 0) {
                (currentValProfit.toDouble() / stock.buyMoney.toDouble()) * 100
            } else {
                0.0
            }

            stock.copy(
                valProfit = currentValProfit,
                profitNum = currentProfitNum,
                updatedAt = LocalDateTime.now()
            )
        }
        commandStockPort.saveAll(stocksToUpdate)
    }
}
