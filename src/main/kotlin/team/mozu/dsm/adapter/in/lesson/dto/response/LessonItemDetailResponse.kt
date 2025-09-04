package team.mozu.dsm.adapter.`in`.lesson.dto.response

import java.util.UUID

data class LessonItemDetailResponse(
    val itemId: UUID,
    val itemName: String,
    val itemLogo: String?,
    val nowMoney: Int,
    val profitMoney: Int,
    val profitNum: Double,
    val moneyList: List<Int>,
    val itemInfo: String,
    val money: Int,
    val debt: Int,
    val capital: Int,
    val profit: Int,
    val profitOg: Int,
    val profitBen: Int,
    val netProfit: Int
)
