package team.mozu.dsm.adapter.out.lesson.persistence

import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.item.entity.QItemJpaEntity.itemJpaEntity
import team.mozu.dsm.adapter.out.item.persistence.repository.ItemRepository
import team.mozu.dsm.adapter.out.lesson.entity.QLessonItemJpaEntity.lessonItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.entity.QLessonJpaEntity.lessonJpaEntity
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonItemMapper
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonItemRepository
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonRepository
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.out.lesson.dto.LessonItemDetailProjection
import team.mozu.dsm.application.port.out.lesson.CommandLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.dto.LessonRoundItemProjection
import team.mozu.dsm.application.port.out.lesson.dto.QLessonItemDetailProjection
import team.mozu.dsm.application.port.out.lesson.dto.QLessonRoundItemProjection
import team.mozu.dsm.domain.lesson.model.LessonItem
import java.util.UUID

@Component
class LessonItemPersistenceAdapter(
    private val lessonRepository: LessonRepository,
    private val lessonItemRepository: LessonItemRepository,
    private val lessonItemMapper: LessonItemMapper,
    private val itemRepository: ItemRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : QueryLessonItemPort, CommandLessonItemPort {

    //--Query--//
    override fun findItemIdsByLessonId(lessonId: UUID): List<UUID> {
        return jpaQueryFactory
            .select(lessonItemJpaEntity.lessonItemId.itemId)
            .from(lessonItemJpaEntity)
            .where(lessonItemJpaEntity.lessonItemId.lessonId.eq(lessonId))
            .fetch()
    }

    override fun findAllByLessonIdAndItemIds(lessonId: UUID, itemIds: List<UUID>): List<LessonItem> {
        if (itemIds.isEmpty()) return emptyList()
        val entities = jpaQueryFactory
            .selectFrom(lessonItemJpaEntity)
            .where(
                lessonItemJpaEntity.lessonItemId.lessonId.eq(lessonId)
                    .and(lessonItemJpaEntity.lessonItemId.itemId.`in`(itemIds))
            )
            .fetch()

        return entities.map { lessonItemMapper.toModel(it) }
    }

    override fun findAllByLessonId(lessonId: UUID): List<LessonItem> {
        return lessonItemRepository.findAllByLessonId(lessonId)
            .map { lessonItemMapper.toModel(it) }
    }

    override fun findAllRoundItemsByLessonId(lessonId: UUID): List<LessonRoundItemProjection> {
        return jpaQueryFactory
            .select(
                QLessonRoundItemProjection(
                    itemJpaEntity.id,
                    itemJpaEntity.itemName,
                    itemJpaEntity.itemLogo,
                    getPreMoneyCase(),
                    getCurMoneyCase()
                )
            )
            .from(lessonItemJpaEntity)
            .join(lessonItemJpaEntity.lesson, lessonJpaEntity)
            .join(lessonItemJpaEntity.item, itemJpaEntity)
            .where(
                lessonItemJpaEntity.lesson.id.eq(lessonId)
                    .and(lessonItemJpaEntity.lesson.isInProgress.isTrue)
            )
            .fetch()
    }

    override fun findItemDetailByLessonIdAndItemId(lessonId: UUID, itemId: UUID): LessonItemDetailProjection {
        return jpaQueryFactory
            .select(
                QLessonItemDetailProjection(
                    getPreMoneyCase(),
                    getCurMoneyCase(),
                    lessonItemJpaEntity.currentMoney,
                    lessonItemJpaEntity.round1Money,
                    lessonItemJpaEntity.round2Money,
                    lessonItemJpaEntity.round3Money,
                    lessonItemJpaEntity.round4Money,
                    lessonItemJpaEntity.round5Money
                )
            )
            .from(lessonItemJpaEntity)
            .join(lessonItemJpaEntity.lesson, lessonJpaEntity)
            .where(
                lessonItemJpaEntity.lesson.id.eq(lessonId)
                    .and(lessonItemJpaEntity.item.id.eq(itemId))
                    .and(lessonJpaEntity.isInProgress.isTrue)
            )
            .fetchOne() ?: throw ItemNotFoundException
    }

    //--Command--//
    override fun saveAll(id: UUID, lessonItems: List<LessonItem>): List<LessonItem> {
        val lessonEntity = lessonRepository.findById(id).orElse(null)
            ?: throw LessonNotFoundException

        /**
         * LessonItemList 저장 프로세스
         * 1) lessonItems에서 모든 itemId 추출 후 DB에서 한 번에 조회 (DB 성능 최적화를 위해)
         * 2) .associateBy를 사용하여 Map<UUID, ItemJpaEntity> 형식으로 변환
         * 3) 각 LessonItem 도메인 모델을 Lesson, Item과 매핑하여 JPA 엔티티 생성
         * 4) 생성된 엔티티를 saveAll로 저장 후 도메인 모델로 변환
         */
        val lessonItemList = itemRepository.findAllById(lessonItems.map { it.lessonItemId.itemId })
            .associateBy { it.id }
            .let { itemMap ->
                lessonItems.map { model ->
                    val itemEntity = itemMap[model.lessonItemId.itemId]
                        ?: throw ItemNotFoundException

                    lessonItemMapper.toEntity(model, lessonEntity, itemEntity)
                }
            }
        lessonItemRepository.saveAll(lessonItemList)

        return lessonItemList.map { entity -> lessonItemMapper.toModel(entity) }
    }

    override fun deleteAll(lessonId: UUID) {
        // lessonId 기준으로 기존 LessonItem 전부 삭제
        jpaQueryFactory
            .delete(lessonItemJpaEntity)
            .where(lessonItemJpaEntity.lesson.id.eq(lessonId))
            .execute()
    }

    //--CaseBuilder--//
    private fun getPreMoneyCase(): NumberExpression<Int> {
        return CaseBuilder()
            .`when`(lessonJpaEntity.curInvRound.eq(1)).then(0)
            .`when`(lessonJpaEntity.curInvRound.eq(2)).then(lessonItemJpaEntity.currentMoney)
            .`when`(lessonJpaEntity.curInvRound.eq(3)).then(lessonItemJpaEntity.round1Money)
            .`when`(lessonJpaEntity.curInvRound.eq(4)).then(lessonItemJpaEntity.round2Money)
            .`when`(lessonJpaEntity.curInvRound.eq(5)).then(lessonItemJpaEntity.round3Money)
            .otherwise(0)
    }

    private fun getCurMoneyCase(): NumberExpression<Int> {
        return CaseBuilder()
            .`when`(lessonJpaEntity.curInvRound.eq(1)).then(lessonItemJpaEntity.currentMoney)
            .`when`(lessonJpaEntity.curInvRound.eq(2)).then(lessonItemJpaEntity.round1Money)
            .`when`(lessonJpaEntity.curInvRound.eq(3)).then(lessonItemJpaEntity.round2Money)
            .`when`(lessonJpaEntity.curInvRound.eq(4)).then(lessonItemJpaEntity.round3Money)
            .`when`(lessonJpaEntity.curInvRound.eq(5)).then(lessonItemJpaEntity.round4Money.coalesce(0))
            .otherwise(0)
    }
}
