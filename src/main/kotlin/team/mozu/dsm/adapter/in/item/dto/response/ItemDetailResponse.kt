package team.mozu.dsm.adapter.`in`.item.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import team.mozu.dsm.domain.item.model.Item
import java.time.LocalDateTime

data class ItemDetailResponse(
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
) {
    companion object {
        fun from(item: Item): ItemDetailResponse {
            return ItemDetailResponse(
                itemId = item.id!!,
                itemName = item.itemName,
                itemLogo = item.itemLogo,
                itemInfo = item.itemInfo,
                money = item.money,
                debt = item.debt,
                capital = item.capital,
                profit = item.profit,
                profitOg = item.profitOg,
                profitBenefit = item.profitBenefit,
                netProfit = item.netProfit,
                isDeleted = item.isDeleted,
                createdAt = item.createdAt
            )
        }
    }
}
