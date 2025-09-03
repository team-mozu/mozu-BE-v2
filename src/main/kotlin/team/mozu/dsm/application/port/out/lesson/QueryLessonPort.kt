package team.mozu.dsm.application.port.out.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonSummaryResponse
import team.mozu.dsm.domain.lesson.model.Lesson
import java.util.UUID

interface QueryLessonPort {

    fun findByLessonNum(lessonNum: String): Lesson?

    fun findById(id: UUID): Lesson?

    fun existsByLessonNum(lessonNum: String): Boolean

    fun findAllByOrganId(organId: UUID): List<LessonSummaryResponse>
}
