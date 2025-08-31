package team.mozu.dsm.adapter.out.lesson.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.entity.QLessonItemJpaEntity.lessonItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonItemMapper
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.domain.lesson.model.LessonItem
import java.util.UUID

@Component
class PersistenceAdapterLessonItem(
    private val lessonItemMapper: LessonItemMapper,
    private val queryFactory: JPAQueryFactory
) : QueryLessonItemPort {

    override fun findItemIdsByLessonId(lessonId: UUID): List<UUID> {
        return queryFactory
            .select(lessonItemJpaEntity.lessonItemId.itemId)
            .from(lessonItemJpaEntity)
            .where(lessonItemJpaEntity.lessonItemId.lessonId.eq(lessonId))
            .fetch()
    }

    override fun findAllByLessonIdAndItemIds(lessonId: UUID, itemIds: List<UUID>): List<LessonItem> {
        if (itemIds.isEmpty()) return emptyList()
        val entities = queryFactory
            .selectFrom(lessonItemJpaEntity)
            .where(
                lessonItemJpaEntity.lessonItemId.lessonId.eq(lessonId)
                    .and(lessonItemJpaEntity.lessonItemId.itemId.`in`(itemIds))
            )
            .fetch()

        return entities.map { lessonItemMapper.toModel(it) }
    }
}
