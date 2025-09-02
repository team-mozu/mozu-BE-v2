package team.mozu.dsm.adapter.`in`.team.dto.response

import java.util.UUID

data class StockResponse(
    val id: UUID,
    val itemId: UUID,
    val itemName: String,
    val avgPurchasePrice: Int,
    val quantity: Int,
    val totalMoney: Int,
    val nowMoney: Int,
    val valuationMoney: Int,
    val valProfit: Int,
    val profitNum: Double
)
