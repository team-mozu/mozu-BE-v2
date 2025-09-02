package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.adapter.`in`.team.dto.response.StockResponse
import java.util.UUID

interface GetStocksUseCase {

    fun getStocks(lessonNum: String, teamId: UUID): List<StockResponse>
}
