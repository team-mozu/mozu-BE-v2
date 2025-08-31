package team.mozu.dsm.domain.lesson.model

import team.mozu.dsm.domain.exception.lesson.InvalidLessonRoundException
import team.mozu.dsm.domain.annotation.Aggregate
import team.mozu.dsm.domain.lesson.model.id.LessonItemId

@Aggregate
data class LessonItem(
    val lessonItemId: LessonItemId,
    val currentMoney: Int,
    val round1Money: Int,
    val round2Money: Int,
    val round3Money: Int,
    val round4Money: Int?,
    val round5Money: Int?
) {
    /**
     * 특정 차수의 가격 조회
     */
    fun getPriceByRound(round: Int): Int? {
        return when (round) {
            1 -> round1Money
            2 -> round2Money
            3 -> round3Money
            4 -> round4Money
            5 -> round5Money
            else -> throw InvalidLessonRoundException
        }
    }
}
