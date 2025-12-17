package team.mozu.dsm.domain.lesson.model

import team.mozu.dsm.domain.annotation.Aggregate
import team.mozu.dsm.domain.lesson.model.id.LessonItemId

@Aggregate
data class LessonItem(
    val lessonItemId: LessonItemId,
    val round1Price: Long,
    val round2Price: Long,
    val round3Price: Long,
    val round4Price: Long?,
    val round5Price: Long?,
    val endPrice: Long
) {
    /**
     * 특정 차수의 가격 조회
     */
    fun getPriceByRound(round: Int): Long? {
        return when (round) {
            0 -> endPrice
            1 -> round1Price
            2 -> round2Price
            3 -> round3Price
            4 -> round4Price
            5 -> round5Price
            else -> endPrice // 6차 이상인 경우 종료가로 fallback
        }
    }
}
