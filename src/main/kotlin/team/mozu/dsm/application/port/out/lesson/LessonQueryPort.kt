package team.mozu.dsm.application.port.out.lesson

import team.mozu.dsm.domain.lesson.model.Lesson
import java.util.UUID

interface LessonQueryPort {

    fun findByLessonNum(lessonNum: String): Lesson?

    fun findById(lessonId: UUID): Lesson?
}
