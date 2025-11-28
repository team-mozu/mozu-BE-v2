package team.mozu.dsm.adapter.`in`.team.dto.response

import java.util.UUID

data class TeamResultResponse(
    val id: UUID,
    val teamName: String?,
    val baseMoney: Long,
    val totalMoney: Long,
    val investingMoney: Long, // 투자중인 금액 (매입한 주식 총액)
    val availableMoney: Long, // 주문 가능 금액 (보유 현금)
    val invRound: Int,
    val valProfit: Long,
    val profitNum: String,
    val orderCount: Int
)
