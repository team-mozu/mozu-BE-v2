package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.StockResponse
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.GetHoldStockUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.UUID

@Service
class GetHoldStockService(
    private val queryStockPort: QueryStockPort,
    private val queryLessonPort: QueryLessonPort,
    private val queryLessonItemPort: QueryLessonItemPort,
    private val queryTeamPort: QueryTeamPort
) : GetHoldStockUseCase {

    @Transactional(readOnly = true)
    override fun getHoldStock(teamId: UUID): List<StockResponse> {
        return try {
            println("GetHoldStockService - Starting request for teamId: $teamId")

            val team = queryTeamPort.findById(teamId) ?: run {
                println("Team not found for teamId: $teamId")
                throw TeamNotFoundException
            }
            println("Found team with lessonNum: ${team.lessonNum}")

            val lesson = queryLessonPort.findByLessonNum(team.lessonNum) ?: run {
                println("Lesson not found for lessonNum: ${team.lessonNum}")
                throw LessonNotFoundException
            }
            println("Found lesson with curInvRound: ${lesson.curInvRound}")

            val stocks = queryStockPort.findAllByTeamId(teamId)
                .filter { it.id != null && it.quantity > 0 }
            println("Found ${stocks.size} stocks for teamId: $teamId")

            if (stocks.isEmpty()) {
                println("No stocks found, returning empty list")
                return emptyList()
            }

            val lessonItemMap = queryLessonItemPort
                .findAllByLessonIdAndItemIds(lesson.id!!, stocks.map { it.itemId }.distinct())
                .associateBy { it.lessonItemId.itemId }

            val currentRound = lesson.curInvRound.coerceAtLeast(0)
            val previousRound = if (currentRound > 1) currentRound - 1 else 1

            stocks.mapNotNull { stock ->
                val lessonItem = lessonItemMap[stock.itemId] ?: return@mapNotNull null

                val currentPrice = lessonItem.getPriceByRound(currentRound) ?: lessonItem.currentMoney
                val previousPrice = lessonItem.getPriceByRound(previousRound) ?: lessonItem.currentMoney

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
        } catch (e: Exception) {
            println("GetHoldStockService Error - teamId: $teamId, error: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}
