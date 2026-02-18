package team.mozu.dsm.adapter.`in`.team.dto.response

import team.mozu.dsm.domain.lesson.model.LessonItem
import team.mozu.dsm.domain.team.model.Stock
import java.util.UUID

data class StockResponse(
    val id: UUID,
    val itemId: Int,
    val itemName: String,
    val avgPurchasePrice: Long,
    val quantity: Int,
    val totalMoney: Long,
    val nowMoney: Long,
    val valuationMoney: Long,
    val valProfit: Long,
    val profitNum: Double
) {
    companion object {
        fun of(stock: Stock, lessonItem: LessonItem, currentRound: Int, previousRound: Int): StockResponse {
            val currentPrice = lessonItem.getPriceByRound(currentRound) ?: lessonItem.endPrice
            val previousPrice = lessonItem.getPriceByRound(previousRound) ?: lessonItem.endPrice

            val currentValuation = currentPrice * stock.quantity
            val previousValuation = previousPrice * stock.quantity
            val valProfit = currentValuation - previousValuation

            val profitNum = if (previousValuation > 0) {
                (valProfit.toDouble() / previousValuation.toDouble()) * 100
            } else {
                0.0
            }

            return StockResponse(
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
