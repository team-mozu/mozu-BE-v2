package team.mozu.dsm.adapter.`in`.team.dto.response

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
)
