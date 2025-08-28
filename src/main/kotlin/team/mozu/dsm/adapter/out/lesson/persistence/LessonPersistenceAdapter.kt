package team.mozu.dsm.adapter.out.lesson.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonMapper
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonRepository
import team.mozu.dsm.application.port.out.lesson.LessonQueryPort
import team.mozu.dsm.domain.lesson.model.Lesson
import java.util.*

@Component
class LessonPersistenceAdapter(
    private val lessonRepository: LessonRepository,
    private val lessonMapper: LessonMapper
) : LessonQueryPort {
    override fun findByLessonNum(lessonNum: String): Lesson? {
        return lessonRepository.findByLessonNum(lessonNum)
            ?.let { lessonMapper.toModel(it) }
    }
}
