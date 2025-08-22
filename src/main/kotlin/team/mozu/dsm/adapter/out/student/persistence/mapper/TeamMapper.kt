package team.mozu.dsm.adapter.out.student.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.`class`.entity.ClassJpaEntity
import team.mozu.dsm.adapter.out.student.entity.TeamJpaEntity
import team.mozu.dsm.domain.student.model.Team

@Mapper(componentModel = "spring")
interface TeamMapper {

    @Mapping(target = "classId", source = "class.id")
    fun toModel(entity: TeamJpaEntity): Team

    fun toEntity(model: Team, `class`: ClassJpaEntity): TeamJpaEntity
}
