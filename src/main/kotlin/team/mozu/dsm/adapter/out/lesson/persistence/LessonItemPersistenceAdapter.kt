package team.mozu.dsm.adapter.out.lesson.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.entity.QLessonItemJpaEntity.lessonItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonItemMapper
import team.mozu.dsm.application.port.out.lesson.LessonItemQueryPort
import team.mozu.dsm.domain.lesson.model.LessonItem
import java.util.UUID

@Component
class LessonItemPersistenceAdapter(
    private val lessonItemMapper: LessonItemMapper,
    private val queryFactory: JPAQueryFactory
) : LessonItemQueryPort {

    private val li = lessonItemJpaEntity

    override fun findItemIdsByLessonId(lessonId: UUID): List<UUID> {
        return queryFactory
            .select(li.lessonItemId.itemId)
            .from(li)
            .where(li.lessonItemId.lessonId.eq(lessonId))
            .fetch()
    }

    override fun findAllByLessonIdAndItemIds(lessonId: UUID, itemIds: List<UUID>): List<LessonItem> {
        if (itemIds.isEmpty()) return emptyList()
        val entities = queryFactory
            .selectFrom(li)
            .where(
                li.lessonItemId.lessonId.eq(lessonId)
                    .and(li.lessonItemId.itemId.`in`(itemIds))
            )
            .fetch()

        return entities.map { lessonItemMapper.toModel(it) }
    }
}
