package team.mozu.dsm.application.port.out.lesson

import team.mozu.dsm.domain.lesson.model.Lesson

interface QueryLessonPort {

    fun findByLessonNum(lessonNum: String): Lesson?
}
