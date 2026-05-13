package team.mozu.dsm.adapter.out.team.mapper

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.entity.LessonJpaEntity
import team.mozu.dsm.adapter.out.team.entity.RoundSnapshotJpaEntity
import team.mozu.dsm.adapter.out.team.entity.TeamJpaEntity
import team.mozu.dsm.domain.team.model.RoundSnapshot

@Component
class RoundSnapshotMapper {

    fun toModel(entity: RoundSnapshotJpaEntity): RoundSnapshot = RoundSnapshot(
        id = entity.id,
        lessonId = entity.lesson.id!!,
        teamId = entity.team.id!!,
        invRound = entity.invRound,
        totalMoney = entity.totalMoney,
        cashMoney = entity.cashMoney,
        valuationMoney = entity.valuationMoney,
        profitNum = entity.profitNum,
        endedAt = entity.endedAt,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )

    fun toEntity(model: RoundSnapshot, lesson: LessonJpaEntity, team: TeamJpaEntity): RoundSnapshotJpaEntity =
        RoundSnapshotJpaEntity(
            invRound = model.invRound,
            totalMoney = model.totalMoney,
            cashMoney = model.cashMoney,
            valuationMoney = model.valuationMoney,
            profitNum = model.profitNum,
            endedAt = model.endedAt,
            lesson = lesson,
            team = team
        )
}
