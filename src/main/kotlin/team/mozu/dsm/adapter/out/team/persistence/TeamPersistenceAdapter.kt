package team.mozu.dsm.adapter.out.team.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonRepository
import team.mozu.dsm.adapter.out.team.entity.QTeamJpaEntity
import team.mozu.dsm.adapter.out.team.persistence.mapper.TeamMapper
import team.mozu.dsm.adapter.out.team.persistence.repository.TeamRepository
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.out.team.TeamCommandPort
import team.mozu.dsm.application.port.out.team.TeamQueryPort
import team.mozu.dsm.domain.team.model.Team
import java.util.*

@Component
class TeamPersistenceAdapter(
    private val teamRepository: TeamRepository,
    private val lessonRepository: LessonRepository,
    private val teamMapper: TeamMapper,
    private val queryFactory: JPAQueryFactory
) : TeamCommandPort, TeamQueryPort {

    private val t = QTeamJpaEntity.teamJpaEntity

    //--Query--//
    override fun findById(teamId: UUID): Team {
        val entity = teamRepository.findById(teamId)
            .orElseThrow { TeamNotFoundException }

        return teamMapper.toModel(entity)
    }

    override fun findByIdWithLock(teamId: UUID): Team? {
        val entity = queryFactory
            .selectFrom(t)
            .where(t.id.eq(teamId))
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()

        return entity?.let { teamMapper.toModel(it) }
    }

    //--Command--//
    override fun save(team: Team): Team {
        val lessonEntity = lessonRepository.findById(team.lessonId)
            .orElseThrow { LessonNotFoundException }

        val entity = if (team.id != null) {
            val existingEntity = teamRepository.findById(team.id)
                .orElseThrow { TeamNotFoundException }

            existingEntity.apply {
                cashMoney = team.cashMoney
                valuationMoney = team.valuationMoney
                totalMoney = team.totalMoney
                isInvestmentInProgress = team.isInvestmentInProgress
                updatedAt = team.updatedAt
            }
        } else {
            teamMapper.toEntity(team, lessonEntity)
        }

        val savedEntity = teamRepository.save(entity)
        return teamMapper.toModel(savedEntity)
    }
}
