package team.mozu.dsm.adapter.`in`.lesson.dto.response

data class LessonRoundItemResponse(
    val itemId: Int,
    val itemName: String,
    val itemLogo: String?,
    val nowMoney: Int,
    val profitMoney: Int,
    val profitNum: Double
)
