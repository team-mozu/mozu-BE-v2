package team.mozu.dsm.domain.item.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class Item(
    val id: Int?,
    val organId: UUID,
    val itemName: String,
    val itemLogo: String?,
    val itemInfo: String,
    val money: Long,
    val debt: Long,
    val capital: Long,
    val profit: Long,
    val profitOg: Long,
    val profitBenefit: Long,
    val netProfit: Long,
    val isDeleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
