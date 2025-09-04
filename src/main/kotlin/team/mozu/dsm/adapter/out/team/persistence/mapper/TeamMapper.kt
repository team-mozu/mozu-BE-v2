package team.mozu.dsm.adapter.out.team.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.lesson.entity.LessonJpaEntity
import team.mozu.dsm.adapter.out.team.entity.TeamJpaEntity
import team.mozu.dsm.domain.team.model.Team

@Mapper(componentModel = "spring")
abstract class TeamMapper {

    @Mapping(target = "lessonId", source = "lesson.id")
    abstract fun toModel(entity: TeamJpaEntity): Team

    fun toEntity(model: Team, lesson: LessonJpaEntity): TeamJpaEntity {
        return TeamJpaEntity(
            lesson = lesson,
            teamName = model.teamName,
            schoolName = model.schoolName,
            totalMoney = model.totalMoney,
            cashMoney = model.cashMoney,
            valuationMoney = model.valuationMoney,
            lessonNum = model.lessonNum,
            isInvestmentInProgress = model.isInvestmentInProgress
        )
    }
}
