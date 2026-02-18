package team.mozu.dsm.adapter.`in`.lesson.dto.response

import team.mozu.dsm.application.port.out.lesson.dto.LessonRoundItemProjection
import kotlin.math.roundToInt

data class LessonRoundItemResponse(
    val itemId: Int,
    val itemName: String,
    val itemLogo: String?,
    val nowMoney: Long,
    val profitMoney: Long,
    val profitNum: String
) {
    companion object {
        fun from(projection: LessonRoundItemProjection): LessonRoundItemResponse {
            val profitMoney = if (projection.preMoney != 0L) {
                projection.curMoney - projection.preMoney
            } else {
                0L
            }

            val profitNum = if (projection.preMoney != 0L) {
                ((profitMoney.toDouble() * 100 / projection.preMoney) * 100).roundToInt() / 100.0
            } else {
                0.0
            }

            val formattedProfitNum = if (profitNum > 0) "+$profitNum%" else "$profitNum%"

            return LessonRoundItemResponse(
                itemId = projection.itemId,
                itemName = projection.itemName,
                itemLogo = projection.itemLogo,
                nowMoney = projection.curMoney,
                profitMoney = profitMoney,
                profitNum = formattedProfitNum
            )
        }
    }
}
