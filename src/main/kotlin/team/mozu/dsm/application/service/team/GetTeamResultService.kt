package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamResultResponse
import team.mozu.dsm.application.exception.lesson.LessonItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.`in`.team.GetTeamResultUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.QueryOrderItemPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.UUID

@Service
class GetTeamResultService(
    private val queryLessonPort: QueryLessonPort,
    private val queryTeamPort: QueryTeamPort,
    private val queryStockPort: QueryStockPort,
    private val queryLessonItemPort: QueryLessonItemPort,
    private val queryOrderItemPort: QueryOrderItemPort
) : GetTeamResultUseCase {

    @Transactional(readOnly = true)
    override fun get(lessonNum: String, teamId: UUID): TeamResultResponse {

        val lesson = queryLessonPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException

        val team = queryTeamPort.findById(teamId)

        val stocks = queryStockPort.findAllByTeamId(teamId)

        val previousInv = lesson.curInvRound - 1

        val lessonItemMap = queryLessonItemPort.findAllByLessonIdAndItemIds(
            lesson.id!!,
            stocks.map { it.itemId }.distinct()
        ).associateBy { it.lessonItemId.itemId }

        val totalBuyMoney = stocks.sumOf { it.buyMoney }

        val valProfit = stocks.sumOf { stock ->
            val lessonItem = lessonItemMap[stock.itemId]
                ?: throw LessonItemNotFoundException

            val currentPrice = lessonItem.getPriceByRound(previousInv) ?: lessonItem.currentMoney
            (currentPrice * stock.quantity) - stock.buyMoney
        }

        val profitNum = if (totalBuyMoney > 0) {
            (valProfit.toDouble() / totalBuyMoney.toDouble()) * 100
        } else {
            0.0
        }

        return TeamResultResponse(
            id = team.id!!,
            teamName = team.teamName,
            baseMoney = lesson.baseMoney,
            totalMoney = team.totalMoney,
            invRound = lesson.curInvRound,
            valProfit = valProfit,
            profitNum = profitNum,
            orderCount = queryOrderItemPort.countOrderItemsByTeamId(teamId)
        )
    }
}
