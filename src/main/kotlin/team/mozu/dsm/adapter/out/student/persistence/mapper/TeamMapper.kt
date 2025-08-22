package team.mozu.dsm.adapter.out.student.persistence.mapper

import org.mapstruct.Mapper
import team.mozu.dsm.adapter.out.`class`.entity.ClassJpaEntity
import team.mozu.dsm.adapter.out.student.entity.TeamJpaEntity
import team.mozu.dsm.domain.student.model.Team

@Mapper(componentModel = "spring")
class TeamMapper {

    fun toModel(entity: TeamJpaEntity): Team {
        return Team(
            id = entity.id,
            classId = entity.`class`.id!!,
            teamName = entity.teamName,
            schoolName = entity.schoolName,
            totalMoney = entity.totalMoney,
            cashMoney = entity.cashMoney,
            valuationMoney = entity.valuationMoney,
            classNumber = entity.classNumber,
            investmentCompleteYn = entity.investmentCompleteYn,
            participationDate = entity.participationDate,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(model: Team, `class`: ClassJpaEntity): TeamJpaEntity {
        return TeamJpaEntity(
            `class` = `class`,
            teamName = model.teamName,
            schoolName = model.schoolName,
            totalMoney = model.totalMoney,
            cashMoney = model.cashMoney,
            valuationMoney = model.valuationMoney,
            classNumber = model.classNumber,
            investmentCompleteYn = model.investmentCompleteYn,
            participationDate = model.participationDate
        )
    }
}
