package team.mozu.dsm.domain.lesson.model

import team.mozu.dsm.domain.exception.lesson.InvalidLessonRoundException
import team.mozu.dsm.domain.annotation.Aggregate
import team.mozu.dsm.domain.lesson.model.id.LessonItemId

@Aggregate
data class LessonItem(
    val lessonItemId: LessonItemId,
    val currentMoney: Long,
    val round1Money: Long,
    val round2Money: Long,
    val round3Money: Long,
    val round4Money: Long?,
    val round5Money: Long?
) {
    /**
     * 특정 차수의 가격 조회
     */
    fun getPriceByRound(round: Int): Long? {
        return when (round) {
            0 -> currentMoney
            1 -> round1Money
            2 -> round2Money
            3 -> round3Money
            4 -> round4Money
            5 -> round5Money
            else -> throw InvalidLessonRoundException
        }
    }
}
