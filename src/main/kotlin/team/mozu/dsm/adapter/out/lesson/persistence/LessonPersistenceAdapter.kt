package team.mozu.dsm.adapter.out.lesson.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonMapper
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonRepository
import team.mozu.dsm.adapter.out.organ.persistence.repository.OrganRepository
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.domain.lesson.model.Lesson

@Component
class LessonPersistenceAdapter(
    private val lessonRepository: LessonRepository,
    private val lessonMapper: LessonMapper,
    private val organRepository: OrganRepository
) : QueryLessonPort, CommandLessonPort {

    //--Query--//
    override fun findByLessonNum(lessonNum: String): Lesson? {
        return lessonRepository.findByLessonNum(lessonNum)
            ?.let { lessonMapper.toModel(it) }
    }

    //--Command--//
    override fun save(lesson: Lesson): Lesson {
        val organ = organRepository.findById(lesson.organId)
            .orElseThrow { OrganNotFoundException }

        val entity = lessonMapper.toEntity(lesson, organ)
        lessonRepository.save(entity)

        return lessonMapper.toModel(entity)
    }
}
