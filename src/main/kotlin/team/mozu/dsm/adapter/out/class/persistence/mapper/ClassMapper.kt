package team.mozu.dsm.adapter.out.`class`.persistence.mapper

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.`class`.entity.ClassJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.`class`.model.Class

@Component
class ClassMapper {

    fun toModel(entity: ClassJpaEntity): Class {
        return Class(
            id = entity.id,
            organId = entity.organ.id!!,
            className = entity.className,
            maxInvRound = entity.maxInvRound,
            curInvRound = entity.curInvRound,
            baseMoney = entity.baseMoney,
            classNum = entity.classNum,
            isStarred = entity.isStarred,
            isDeleted = entity.isDeleted,
            isInProgress = entity.isInProgress,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(model: Class, organ: OrganJpaEntity): ClassJpaEntity {
        return ClassJpaEntity(
            organ = organ,
            className = model.className,
            maxInvRound = model.maxInvRound,
            curInvRound = model.curInvRound,
            baseMoney = model.baseMoney,
            classNum = model.classNum,
            isStarred = model.isStarred,
            isDeleted = model.isDeleted,
            isInProgress = model.isInProgress
        )
    }
}
