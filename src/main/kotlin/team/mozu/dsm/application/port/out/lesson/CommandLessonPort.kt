package team.mozu.dsm.application.port.out.lesson

import team.mozu.dsm.domain.lesson.model.Lesson

interface CommandLessonPort {

    fun save(lesson: Lesson): Lesson
}
