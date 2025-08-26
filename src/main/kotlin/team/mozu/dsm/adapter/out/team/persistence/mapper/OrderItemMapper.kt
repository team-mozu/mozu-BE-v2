package team.mozu.dsm.adapter.out.team.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.item.entity.ItemJpaEntity
import team.mozu.dsm.adapter.out.team.entity.OrderItemJpaEntity
import team.mozu.dsm.adapter.out.team.entity.TeamJpaEntity
import team.mozu.dsm.domain.team.model.OrderItem

@Mapper(componentModel = "spring")
abstract class OrderItemMapper {

    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "itemId", source = "item.id")
    abstract fun toModel(entity: OrderItemJpaEntity): OrderItem

    fun toEntity(model: OrderItem, item: ItemJpaEntity, team: TeamJpaEntity): OrderItemJpaEntity {
        return OrderItemJpaEntity(
            item = item,
            team = team,
            itemName = model.itemName,
            orderType = model.orderType,
            orderCount = model.orderCount,
            itemPrice = model.itemPrice,
            totalAmount = model.totalAmount,
            invCount = model.invCount
        )
    }
}
