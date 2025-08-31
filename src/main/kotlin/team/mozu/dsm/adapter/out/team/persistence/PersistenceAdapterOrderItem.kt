package team.mozu.dsm.adapter.out.team.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.item.persistence.repository.ItemRepository
import team.mozu.dsm.adapter.out.team.persistence.mapper.OrderItemMapper
import team.mozu.dsm.adapter.out.team.persistence.repository.OrderItemRepository
import team.mozu.dsm.adapter.out.team.persistence.repository.TeamRepository
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.out.team.CommandOrderItemPort
import team.mozu.dsm.domain.team.model.OrderItem

@Component
class PersistenceAdapterOrderItem(
    private val orderItemRepository: OrderItemRepository,
    private val itemRepository: ItemRepository,
    private val teamRepository: TeamRepository,
    private val orderItemMapper: OrderItemMapper
) : CommandOrderItemPort {

    override fun saveAll(orderItems: List<OrderItem>) {
        if (orderItems.isEmpty()) return

        val teamEntity = teamRepository.findByIdOrNull(orderItems.first().teamId)
            ?: throw TeamNotFoundException

        val itemIds = orderItems.map { it.itemId }.distinct()

        val itemEntitiesById = itemRepository.findAllById(itemIds)
            .associateBy { it.id ?: throw ItemNotFoundException }

        val missingItemIds = itemIds.toSet() - itemEntitiesById.keys
        if (missingItemIds.isNotEmpty()) throw ItemNotFoundException

        val entities = orderItems.map { order ->
            val itemEntity = itemEntitiesById.getValue(order.itemId)
            orderItemMapper.toEntity(order, itemEntity, teamEntity)
        }

        orderItemRepository.saveAll(entities)
    }
}
