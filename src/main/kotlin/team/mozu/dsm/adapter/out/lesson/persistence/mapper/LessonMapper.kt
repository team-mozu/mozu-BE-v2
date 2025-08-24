package team.mozu.dsm.adapter.out.lesson.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.lesson.entity.LessonJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.lesson.model.Lesson

@Mapper(componentModel = "spring")
abstract class LessonMapper {

    @Mapping(target = "organId", source = "entity.organ.id")
    abstract fun toModel(entity: LessonJpaEntity): Lesson

    fun toEntity(model: Lesson, organ: OrganJpaEntity): LessonJpaEntity {
        return LessonJpaEntity(
            organ = organ,
            lessonName = model.lessonName,
            maxInvRound = model.maxInvRound,
            curInvRound = model.curInvRound,
            baseMoney = model.baseMoney,
            lessonNum = model.lessonNum,
            isStarred = model.isStarred,
            isDeleted = model.isDeleted,
            isInProgress = model.isInProgress
        )
    }
}
