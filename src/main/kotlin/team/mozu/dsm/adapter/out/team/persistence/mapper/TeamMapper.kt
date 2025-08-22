package team.mozu.dsm.adapter.out.team.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.`class`.entity.ClassJpaEntity
import team.mozu.dsm.adapter.out.team.entity.TeamJpaEntity
import team.mozu.dsm.domain.team.model.Team

@Mapper(componentModel = "spring")
abstract class TeamMapper {

    @Mapping(target = "classId", expression = "java(entity.getClazz().getId())")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    abstract fun toModel(entity: TeamJpaEntity): Team

    fun toEntity(model: Team, clazz: ClassJpaEntity): TeamJpaEntity {
        return TeamJpaEntity(
            clazz = clazz,
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
