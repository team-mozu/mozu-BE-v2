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

    @Mapping(target = "id", source = "model.id")
    @Mapping(target = "createdAt", source = "model.createdAt")
    @Mapping(target = "updatedAt", source = "model.updatedAt")
    fun toEntity(model: Team, lesson: LessonJpaEntity): TeamJpaEntity
}
