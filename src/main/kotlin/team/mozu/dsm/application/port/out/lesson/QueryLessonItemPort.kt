package team.mozu.dsm.application.port.out.lesson

import team.mozu.dsm.domain.lesson.model.LessonItem
import java.util.UUID

interface QueryLessonItemPort {

    fun findItemIdsByLessonId(lessonId: UUID): List<UUID>

    fun findAllByLessonIdAndItemIds(lessonId: UUID, itemIds: List<UUID>): List<LessonItem>
}
