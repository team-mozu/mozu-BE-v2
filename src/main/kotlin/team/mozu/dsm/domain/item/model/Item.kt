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
    val money: Int,
    val debt: Int,
    val capital: Int,
    val profit: Int,
    val profitOg: Int,
    val profitBenefit: Int,
    val netProfit: Int,
    val isDeleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
