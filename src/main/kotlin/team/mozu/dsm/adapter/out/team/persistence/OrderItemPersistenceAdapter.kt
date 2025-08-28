package team.mozu.dsm.adapter.out.team.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.item.persistence.repository.ItemRepository
import team.mozu.dsm.adapter.out.team.persistence.mapper.OrderItemMapper
import team.mozu.dsm.adapter.out.team.persistence.repository.OrderItemRepository
import team.mozu.dsm.adapter.out.team.persistence.repository.TeamRepository
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.out.team.OrderItemCommandPort
import team.mozu.dsm.domain.team.model.OrderItem

@Component
class OrderItemPersistenceAdapter(
    private val orderItemRepository: OrderItemRepository,
    private val itemRepository: ItemRepository,
    private val teamRepository: TeamRepository,
    private val orderItemMapper: OrderItemMapper
) : OrderItemCommandPort {

    override fun saveAll(orderItems: List<OrderItem>) {
        val teamEntity = teamRepository.findById(orderItems.first().teamId)
            .orElseThrow { TeamNotFoundException }

        val itemIds = orderItems.map { it.itemId }.distinct()

        val itemEntitiesById = itemRepository.findAllById(itemIds)
            .associateBy { it.id }

        val missingItemIds = itemIds.toSet() - itemEntitiesById.keys
        if (missingItemIds.isNotEmpty()) throw ItemNotFoundException

        val entities = orderItems.map { order ->
            val itemEntity = itemEntitiesById.getValue(order.itemId)
            orderItemMapper.toEntity(order, itemEntity, teamEntity)
        }

        orderItemRepository.saveAll(entities)
    }
}
