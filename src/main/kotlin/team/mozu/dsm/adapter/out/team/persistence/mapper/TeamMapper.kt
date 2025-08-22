package team.mozu.dsm.adapter.out.team.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.`class`.entity.ClassJpaEntity
import team.mozu.dsm.adapter.out.team.entity.TeamJpaEntity
import team.mozu.dsm.domain.team.model.Team

@Mapper(componentModel = "spring")
interface TeamMapper {

    @Mapping(target = "classId", source = "class.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    fun toModel(entity: TeamJpaEntity): Team

    fun toEntity(model: Team, clazz: ClassJpaEntity): TeamJpaEntity
}
