package team.mozu.dsm.adapter.`in`.item.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.util.*

data class ItemResponse(
    val itemId: Int,
    val itemName: String,
    val itemLogo: String? = null,
    val itemInfo: String,
    val money: Long,
    val debt: Long,
    val capital: Long,
    val profit: Long,
    val profitOg: Long,
    val profitBenefit: Long,
    val netProfit: Long,
    val isDeleted: Boolean,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val createdAt: LocalDateTime
)
