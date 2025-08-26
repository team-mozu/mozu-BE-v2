package team.mozu.dsm.adapter.out.lesson.persistence.mapper

import org.mapstruct.Mapper
import team.mozu.dsm.adapter.out.lesson.entity.LessonJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.application.exception.lesson.LessonCreatedAtNotFoundException
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.domain.lesson.model.Lesson

@Mapper(componentModel = "spring")
abstract class LessonMapper {

    /**
     * JpaEntity의 Boolean 필드는 컴파일 시 is 접두사가 제거됨
     * 필드명 불일치 -> 자동 매핑 안됨 = 기본값인 false 할당
     * 기본 값 할당을 하기에 기능 구현이 어려워 수동 구현을 함
     */
    fun toModel(entity: LessonJpaEntity): Lesson {
        return Lesson(
            id = entity.id,
            organId = entity.organ.id ?: throw OrganNotFoundException,
            lessonName = entity.lessonName,
            maxInvRound = entity.maxInvRound,
            curInvRound = entity.curInvRound,
            baseMoney = entity.baseMoney,
            lessonNum = entity.lessonNum,
            isStarred = entity.isStarred,
            isDeleted = entity.isDeleted,
            isInProgress = entity.isInProgress,
            createdAt = entity.createdAt ?: throw LessonCreatedAtNotFoundException,
            updatedAt = entity.updatedAt
        )
    }

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
