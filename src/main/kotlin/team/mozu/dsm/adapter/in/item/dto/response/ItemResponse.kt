package team.mozu.dsm.adapter.`in`.item.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.util.*

data class ItemResponse (
    val id: UUID,
    val name: String,
    val logo: String? = null,
    val info: String,
    val money: Int,
    val debt: Int,
    val capital: Int,
    val profit: Int,
    val profitOg: Int,
    val profitBenefit: Int,
    val netProfit: Int,
    val isDeleted: Boolean,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val createdAt: LocalDateTime
)
