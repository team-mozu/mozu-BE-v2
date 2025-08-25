package team.mozu.dsm.adapter.out.team.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.lesson.entity.LessonItemJpaEntity
import team.mozu.dsm.adapter.out.lesson.persistence.mapper.LessonItemMapper
import team.mozu.dsm.adapter.out.team.entity.OrderItemJpaEntity
import team.mozu.dsm.adapter.out.team.entity.TeamJpaEntity
import team.mozu.dsm.domain.team.model.OrderItem

@Mapper(
    componentModel = "spring",
    /**
     * 중첩된 객체 매핑을 위해 다른 매퍼를 참조
     */
    uses = [LessonItemMapper::class]
)
abstract class OrderItemMapper {

    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "lessonItem", source = "lessonItem")
    abstract fun toModel(entity: OrderItemJpaEntity): OrderItem

    fun toEntity(model: OrderItem, lessonItem: LessonItemJpaEntity, team: TeamJpaEntity): OrderItemJpaEntity {
        return OrderItemJpaEntity(
            lessonItem = lessonItem,
            team = team,
            orderType = model.orderType,
            orderCount = model.orderCount,
            itemPrice = model.itemPrice,
            totalAmount = model.totalAmount,
            orderedAt = model.orderedAt,
            invCnt = model.invCnt
        )
    }
}
