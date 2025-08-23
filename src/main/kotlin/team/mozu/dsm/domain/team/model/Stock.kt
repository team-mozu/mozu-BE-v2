package team.mozu.dsm.domain.team.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class Stock(
    val id: UUID?,
    val teamId: UUID,
    val itemName: String,
    val itemCount: Long,
    val buyMoney: Long,
    val valProfit: Long,
    val profitNum: Double,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
