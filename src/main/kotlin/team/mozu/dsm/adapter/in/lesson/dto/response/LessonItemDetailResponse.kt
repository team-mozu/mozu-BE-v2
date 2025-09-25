package team.mozu.dsm.adapter.`in`.lesson.dto.response

data class LessonItemDetailResponse(
    val itemId: Int,
    val itemName: String,
    val itemLogo: String?,
    val nowMoney: Long,
    val profitMoney: Long,
    val profitNum: Double,
    val moneyList: List<Long>,
    val itemInfo: String,
    val money: Long,
    val debt: Long,
    val capital: Long,
    val profit: Long,
    val profitOg: Long,
    val profitBen: Long,
    val netProfit: Long
)
