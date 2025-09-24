package team.mozu.dsm.application.port.out.lesson

import team.mozu.dsm.application.port.out.lesson.dto.LessonItemDetailProjection
import team.mozu.dsm.application.port.out.lesson.dto.LessonRoundItemProjection
import team.mozu.dsm.domain.lesson.model.LessonItem
import java.util.*

interface QueryLessonItemPort {

    fun findItemIdsByLessonId(lessonId: UUID): List<Int>

    fun findAllByLessonIdAndItemIds(lessonId: UUID, itemIds: List<Int>): List<LessonItem>

    fun findAllByLessonId(lessonId: UUID): List<LessonItem>

    fun findAllRoundItemsByLessonId(lessonId: UUID): List<LessonRoundItemProjection>

    fun findItemDetailByLessonIdAndItemId(lessonId: UUID, itemId: Int): LessonItemDetailProjection
}
