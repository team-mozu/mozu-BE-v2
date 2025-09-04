package team.mozu.dsm.adapter.out.lesson.persistence

import com.querydsl.core.types.dsl.CaseBuilder
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
import team.mozu.dsm.application.port.`in`.lesson.command.LessonRoundItemCommand
import team.mozu.dsm.application.port.`in`.lesson.command.QLessonRoundItemCommand
import team.mozu.dsm.application.port.out.lesson.CommandLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.domain.lesson.model.LessonItem
import java.util.*

@Component
class LessonItemPersistenceAdapter(
    private val lessonRepository: LessonRepository,
    private val lessonItemRepository: LessonItemRepository,
    private val lessonItemMapper: LessonItemMapper,
    private val itemRepository: ItemRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : CommandLessonItemPort, QueryLessonItemPort {

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

    override fun findAllRoundItemsByLessonId(lessonId: UUID): List<LessonRoundItemCommand> {
        return jpaQueryFactory
            .select(
                QLessonRoundItemCommand(
                    itemJpaEntity.id,
                    itemJpaEntity.itemName,
                    itemJpaEntity.itemLogo,
                    // 이전 차수 금액 조회
                    CaseBuilder()
                        .`when`(lessonJpaEntity.curInvRound.eq(0)).then(0)
                        .`when`(lessonJpaEntity.curInvRound.eq(1)).then(lessonItemJpaEntity.currentMoney)
                        .`when`(lessonJpaEntity.curInvRound.eq(2)).then(lessonItemJpaEntity.round1Money)
                        .`when`(lessonJpaEntity.curInvRound.eq(3)).then(lessonItemJpaEntity.round2Money)
                        .`when`(lessonJpaEntity.curInvRound.eq(4)).then(lessonItemJpaEntity.round3Money)
                        .`when`(lessonJpaEntity.curInvRound.eq(5)).then(lessonItemJpaEntity.round4Money.coalesce(0))
                        .otherwise(0),
                    // 현재 차수 값 조회
                    CaseBuilder()
                        .`when`(lessonJpaEntity.curInvRound.eq(0)).then(lessonItemJpaEntity.currentMoney)
                        .`when`(lessonJpaEntity.curInvRound.eq(1)).then(lessonItemJpaEntity.round1Money)
                        .`when`(lessonJpaEntity.curInvRound.eq(2)).then(lessonItemJpaEntity.round2Money)
                        .`when`(lessonJpaEntity.curInvRound.eq(3)).then(lessonItemJpaEntity.round3Money)
                        .`when`(lessonJpaEntity.curInvRound.eq(4)).then(lessonItemJpaEntity.round4Money)
                        .`when`(lessonJpaEntity.curInvRound.eq(5)).then(lessonItemJpaEntity.round5Money.coalesce(0))
                        .otherwise(0)
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
}
