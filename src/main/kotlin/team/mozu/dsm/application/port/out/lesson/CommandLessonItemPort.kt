package team.mozu.dsm.application.port.out.lesson

import team.mozu.dsm.domain.lesson.model.LessonItem
import java.util.UUID

interface CommandLessonItemPort {

    fun saveAll(id: UUID, lessonItems: List<LessonItem>): List<LessonItem>
}
