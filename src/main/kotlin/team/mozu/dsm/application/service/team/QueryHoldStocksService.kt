package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.StockResponse
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.QueryHoldStocksUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.QueryStockPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.UUID

@Service
class QueryHoldStocksService(
    private val queryStockPort: QueryStockPort,
    private val queryLessonPort: QueryLessonPort,
    private val queryLessonItemPort: QueryLessonItemPort,
    private val queryTeamPort: QueryTeamPort
) : QueryHoldStocksUseCase {

    @Transactional(readOnly = true)
    override fun execute(teamId: UUID): List<StockResponse> {
        return try {
            val team = queryTeamPort.findById(teamId)
                ?: throw TeamNotFoundException

            val lesson = queryLessonPort.findByLessonNum(team.lessonNum)
                ?: throw LessonNotFoundException

            val stocks = queryStockPort.findAllByTeamId(teamId)
                .filter { it.id != null && it.quantity > 0 }

            if (stocks.isEmpty()) {
                return emptyList()
            }

            val lessonItemMap = queryLessonItemPort
                .findAllByLessonIdAndItemIds(lesson.id!!, stocks.map { it.itemId }.distinct())
                .associateBy { it.lessonItemId.itemId }

            val currentRound = lesson.curInvRound.coerceAtLeast(0)
            val previousRound = if (currentRound > 1) currentRound - 1 else 1

            stocks.mapNotNull { stock ->
                val lessonItem = lessonItemMap[stock.itemId] ?: return@mapNotNull null
                StockResponse.of(stock, lessonItem, currentRound, previousRound)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
