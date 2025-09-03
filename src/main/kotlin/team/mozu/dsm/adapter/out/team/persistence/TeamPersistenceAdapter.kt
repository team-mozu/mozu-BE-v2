package team.mozu.dsm.adapter.out.team.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.LockModeType
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonRepository
import team.mozu.dsm.adapter.out.team.entity.QTeamJpaEntity.teamJpaEntity
import team.mozu.dsm.adapter.out.team.persistence.mapper.TeamMapper
import team.mozu.dsm.adapter.out.team.persistence.repository.TeamRepository
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.out.team.CommandTeamPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import team.mozu.dsm.domain.team.model.Team
import java.util.UUID

@Component
class TeamPersistenceAdapter(
    private val teamRepository: TeamRepository,
    private val lessonRepository: LessonRepository,
    private val teamMapper: TeamMapper,
    private val jpaQueryFactory: JPAQueryFactory
) : CommandTeamPort, QueryTeamPort {

    //--Query--//
    override fun findById(teamId: UUID): Team {
        val entity = teamRepository.findByIdOrNull(teamId)
            ?: throw TeamNotFoundException

        return teamMapper.toModel(entity)
    }

    override fun findByIdWithLock(teamId: UUID): Team? {
        val entity = jpaQueryFactory
            .selectFrom(teamJpaEntity)
            .where(teamJpaEntity.id.eq(teamId))
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()

        return entity?.let { teamMapper.toModel(it) }
    }

    override fun findAllByLessonId(lessonId: UUID): List<Team> {
        val teams = queryFactory
            .selectFrom(teamJpaEntity)
            .where(
                teamJpaEntity.lesson.id.eq(lessonId)
                    .and(teamJpaEntity.isInvestmentInProgress.isTrue)
            )
            .fetch()

        return teams.map { teamMapper.toModel(it) }
    }

    //--Command--//
    override fun create(team: Team): Team {
        val lessonEntity = lessonRepository.findByIdOrNull(team.lessonId)
            ?: throw LessonNotFoundException
        val entity = teamMapper.toEntity(team, lessonEntity)

        val saved = teamRepository.save(entity)

        return teamMapper.toModel(saved)
    }

    override fun update(team: Team): Team {
        val lessonEntity = lessonRepository.findByIdOrNull(team.lessonId)
            ?: throw LessonNotFoundException

        val entity = team.id?.let { id ->
            teamRepository.findByIdOrNull(id)
                ?: throw TeamNotFoundException
        }?.apply {
            cashMoney = team.cashMoney
            valuationMoney = team.valuationMoney
            totalMoney = team.totalMoney
            isInvestmentInProgress = team.isInvestmentInProgress
            updatedAt = team.updatedAt
        } ?: teamMapper.toEntity(team, lessonEntity)

        val savedEntity = teamRepository.save(entity)
        return teamMapper.toModel(savedEntity)
    }
}
