package team.mozu.dsm.adapter.out.lesson.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.lesson.entity.LessonJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.lesson.model.Lesson

@Mapper(componentModel = "spring")
interface LessonMapper {

    @Mapping(target = "organId", source = "entity.organ.id")
    fun toModel(entity: LessonJpaEntity): Lesson

    @Mapping(target = "id", source = "model.id")
    fun toEntity(model: Lesson, organ: OrganJpaEntity): LessonJpaEntity
}
