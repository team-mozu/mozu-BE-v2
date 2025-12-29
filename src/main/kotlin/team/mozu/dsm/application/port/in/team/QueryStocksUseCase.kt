package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.adapter.`in`.team.dto.response.StockResponse
import java.util.UUID

interface QueryStocksUseCase {

    fun execute(lessonNum: String, teamId: UUID): List<StockResponse>
}
