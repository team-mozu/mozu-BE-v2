package team.mozu.dsm.adapter.`in`.lesson.dto.response

import java.util.UUID

data class LessonRoundItemResponse(
    val itemId: UUID,
    val itemName: String,
    val itemLogo: String?,
    val nowMoney: Int,
    val profitMoney: Int,
    val profitNum: Double
)
