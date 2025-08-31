package team.mozu.dsm.adapter.out.lesson.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonMapper
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonRepository
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.domain.lesson.model.Lesson

@Component
class PersistenceAdapterLesson(
    private val lessonRepository: LessonRepository,
    private val lessonMapper: LessonMapper
) : QueryLessonPort {
    override fun findByLessonNum(lessonNum: String): Lesson? {
        return lessonRepository.findByLessonNum(lessonNum)
            ?.let { lessonMapper.toModel(it) }
    }
}
