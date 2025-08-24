package team.mozu.dsm.adapter.out.team.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.lesson.entity.LessonJpaEntity
import team.mozu.dsm.adapter.out.team.entity.TeamJpaEntity
import team.mozu.dsm.domain.team.model.Team

@Mapper(componentModel = "spring")
interface TeamMapper {

    @Mapping(target = "lessonId", source = "entity.lesson.id")
    fun toModel(entity: TeamJpaEntity): Team

    fun toEntity(model: Team, lesson: LessonJpaEntity): TeamJpaEntity {
        return TeamJpaEntity(
            lesson = lesson,
            teamName = model.teamName,
            schoolName = model.schoolName,
            totalMoney = model.totalMoney,
            cashMoney = model.cashMoney,
            valuationMoney = model.valuationMoney,
            classNumber = model.classNumber,
            isInvestmentCompleted = model.isInvestmentCompleted,
            participationDate = model.participationDate
        )
    }
}
