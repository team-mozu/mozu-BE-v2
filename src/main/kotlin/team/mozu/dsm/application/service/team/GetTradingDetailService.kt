package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.TradingDetailResponse
import team.mozu.dsm.application.exception.lesson.LessonItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.`in`.team.GetTradingDetailUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import java.util.UUID

@Service
class GetTradingDetailService(
    private val queryStockPort: QueryStockPort,
    private val queryLessonPort: QueryLessonPort,
    private val queryLessonItemPort: QueryLessonItemPort
) : GetTradingDetailUseCase {

    @Transactional(readOnly = true)
    override fun getTradingDetail(lessonNum: String, teamId: UUID): List<TradingDetailResponse> {
        val lesson = queryLessonPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException

        val validStocks = queryStockPort.findAllByTeamId(teamId)
            .filter { it.id != null && it.quantity > 0 } // 보유 수량이 있는 주식만

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

        return validStocks.map { stock ->
            val lessonItem = lessonItemMap[stock.itemId]
                ?: throw LessonItemNotFoundException

            val currentPrice = lessonItem.getPriceByRound(currentRound)
                ?: lessonItem.endPrice

            val valuationAmount = currentPrice * stock.quantity
            val totalPurchaseAmount = stock.buyMoney // 총 매입금액 사용
            val profitLoss = valuationAmount - totalPurchaseAmount
            val profitLossRate = if (totalPurchaseAmount > 0) {
                (profitLoss.toDouble() / totalPurchaseAmount.toDouble()) * 100
            } else {
                0.0
            }

            TradingDetailResponse(
                itemId = stock.itemId,
                itemName = stock.itemName,
                holdingQuantity = stock.quantity,
                purchasePrice = totalPurchaseAmount, // 총 매입금액
                currentPrice = currentPrice,
                valuationAmount = valuationAmount,
                profitLoss = profitLoss,
                profitLossRate = profitLossRate
            )
        }.sortedByDescending { it.valuationAmount } // 평가금액 높은 순으로 정렬
    }
}
