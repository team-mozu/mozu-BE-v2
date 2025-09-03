package team.mozu.dsm.adapter.out.lesson.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonSummaryResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.QLessonSummaryResponse
import team.mozu.dsm.adapter.out.lesson.entity.QLessonJpaEntity.lessonJpaEntity
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonMapper
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonRepository
import team.mozu.dsm.adapter.out.organ.persistence.repository.OrganRepository
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.lesson.MaxInvestmentRoundReachedException
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.`in`.lesson.command.UpdateLessonCommand
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
            ?.takeIf { !it.isDeleted }
            ?.let { lessonMapper.toModel(it) }
    }

    override fun findById(id: UUID): Lesson? {
        return lessonRepository.findByIdOrNull(id)
            ?.takeIf { !it.isDeleted }
            ?.let { lessonMapper.toModel(it) }
    }

    override fun existsByLessonNum(lessonNum: String): Boolean {
        return lessonRepository.existsByLessonNum(lessonNum)
    }

    override fun findAllByOrganId(organId: UUID): List<LessonSummaryResponse> {
        return jpaQueryFactory
            .select(
                QLessonSummaryResponse(
                    lessonJpaEntity.id,
                    lessonJpaEntity.lessonName,
                    lessonJpaEntity.isStarred,
                    lessonJpaEntity.createdAt
                )
            ).from(lessonJpaEntity)
            .where(lessonJpaEntity.organ.id.eq(organId))
            .fetch()
    }

    //--Command--//
    override fun save(lesson: Lesson): Lesson {
        val organ = organRepository.findById(lesson.organId).orElse(null)
            ?: throw OrganNotFoundException

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

    override fun updateIsInProgress(id: UUID) {
        jpaQueryFactory
            .update(lessonJpaEntity)
            .set(lessonJpaEntity.isInProgress, false)
            .where(lessonJpaEntity.id.eq(id))
            .execute()
    }

    override fun update(lessonId: UUID, command: UpdateLessonCommand) {
        jpaQueryFactory
            .update(lessonJpaEntity)
            .set(lessonJpaEntity.lessonName, command.lessonName)
            .set(lessonJpaEntity.baseMoney, command.baseMoney)
            .set(lessonJpaEntity.maxInvRound, command.lessonRound)
            .where(lessonJpaEntity.id.eq(lessonId))
            .execute()
    }

    override fun updateCurInvRound(id: UUID): Lesson {
        val lessonEntity = lessonRepository.findById(id).orElse(null)
            ?: throw LessonNotFoundException

        if (lessonEntity.curInvRound < lessonEntity.maxInvRound) {
            jpaQueryFactory
                .update(lessonJpaEntity)
                .set(lessonJpaEntity.curInvRound, lessonJpaEntity.curInvRound.add(1))
                .where(lessonJpaEntity.id.eq(id))
                .execute()

            val updatedEntity = lessonRepository.findById(id).orElse(null)
                ?: throw LessonNotFoundException

            return lessonMapper.toModel(updatedEntity)
        } else {
            throw MaxInvestmentRoundReachedException
        }
    }
}
