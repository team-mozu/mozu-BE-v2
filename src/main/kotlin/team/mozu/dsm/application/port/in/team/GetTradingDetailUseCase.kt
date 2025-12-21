package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.adapter.`in`.team.dto.response.TradingDetailResponse
import java.util.UUID

interface GetTradingDetailUseCase {
    fun getTradingDetail(lessonNum: String, teamId: UUID): List<TradingDetailResponse>
}
