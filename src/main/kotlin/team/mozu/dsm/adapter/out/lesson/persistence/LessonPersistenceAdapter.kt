package team.mozu.dsm.adapter.out.lesson.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.entity.QLessonJpaEntity.lessonJpaEntity
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonMapper
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonRepository
import team.mozu.dsm.adapter.out.organ.persistence.repository.OrganRepository
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.domain.lesson.model.Lesson
import java.util.UUID

@Component
class LessonPersistenceAdapter(
    private val lessonRepository: LessonRepository,
    private val lessonMapper: LessonMapper,
    private val organRepository: OrganRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : QueryLessonPort, CommandLessonPort {

    //--Query--//
    override fun findByLessonNum(lessonNum: String): Lesson? {
        return lessonRepository.findByLessonNum(lessonNum)
            ?.let { lessonMapper.toModel(it) }
    }

    override fun findById(id: UUID): Lesson? {
        return lessonRepository.findByIdOrNull(id)
            ?.let { lessonMapper.toModel(it) }
    }

    override fun existsByLessonNum(lessonNum: String): Boolean {
        return lessonRepository.existsByLessonNum(lessonNum)
    }

    //--Command--//
    override fun save(lesson: Lesson): Lesson {
        val organ = organRepository.findById(lesson.organId)
            .orElseThrow { OrganNotFoundException }

        val entity = lessonMapper.toEntity(lesson, organ)
        val saved = lessonRepository.save(entity)

        return lessonMapper.toModel(saved)
    }

    override fun updateLessonNumAndIsInProgress(id: UUID, lessonNum: String) {
        jpaQueryFactory
            .update(lessonJpaEntity)
            .set(lessonJpaEntity.lessonNum, lessonNum)
            .set(lessonJpaEntity.isInProgress, true)
            .where(lessonJpaEntity.id.eq(id))
            .execute()
    }

    override fun updateIsStarred(id: UUID) {
        jpaQueryFactory
            .update(lessonJpaEntity)
            .set(lessonJpaEntity.isStarred, lessonJpaEntity.isStarred.not())
            .where(lessonJpaEntity.id.eq(id))
            .execute()
    }

    override fun delete(id: UUID) {
        jpaQueryFactory
            .update(lessonJpaEntity)
            .set(lessonJpaEntity.isDeleted, true)
            .where(lessonJpaEntity.id.eq(id))
            .execute()
    }
}
