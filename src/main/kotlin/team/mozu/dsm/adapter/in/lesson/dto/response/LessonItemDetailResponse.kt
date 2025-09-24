package team.mozu.dsm.adapter.`in`.lesson.dto.response

data class LessonItemDetailResponse(
    val itemId: Int,
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
