package team.mozu.dsm.adapter.`in`.team.dto.response

import team.mozu.dsm.domain.lesson.model.LessonItem
import team.mozu.dsm.domain.team.model.Stock

data class TradingDetailResponse(
    val itemId: Int,
    val itemName: String,
    val holdingQuantity: Int, // 보유 주식수
    val purchasePrice: Long, // 매입가 (총 매입금액)
    val currentPrice: Long, // 현재가
    val valuationAmount: Long, // 평가금액 (보유 주식수 × 현재가)
    val profitLoss: Long, // 평가손익
    val profitLossRate: Double // 수익률 (%)
) {
    companion object {
        fun of(stock: Stock, lessonItem: LessonItem, currentRound: Int): TradingDetailResponse {
            val currentPrice = lessonItem.getPriceByRound(currentRound) ?: lessonItem.endPrice
            val valuationAmount = currentPrice * stock.quantity
            val totalPurchaseAmount = stock.buyMoney
            val profitLoss = valuationAmount - totalPurchaseAmount

            val profitLossRate = if (totalPurchaseAmount > 0) {
                (profitLoss.toDouble() / totalPurchaseAmount.toDouble()) * 100
            } else {
                0.0
            }

            return TradingDetailResponse(
                itemId = stock.itemId,
                itemName = stock.itemName,
                holdingQuantity = stock.quantity,
                purchasePrice = totalPurchaseAmount,
                currentPrice = currentPrice,
                valuationAmount = valuationAmount,
                profitLoss = profitLoss,
                profitLossRate = profitLossRate
            )
        }
    }
}
