package team.mozu.dsm.domain.team.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class Stock(
    val id: UUID?,
    val teamId: UUID,
    val itemId: Int,
    val itemName: String,
    val avgPurchasePrice: Int,
    val quantity: Int,
    val buyMoney: Int,
    val valProfit: Int,
    val profitNum: Double,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
