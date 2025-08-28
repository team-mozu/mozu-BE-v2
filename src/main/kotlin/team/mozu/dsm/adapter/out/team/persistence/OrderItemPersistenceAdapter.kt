package team.mozu.dsm.adapter.out.team.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.`in`.team.dto.request.CompleteInvestmentRequest
import team.mozu.dsm.adapter.out.item.persistence.repository.ItemRepository
import team.mozu.dsm.adapter.out.team.persistence.mapper.OrderItemMapper
import team.mozu.dsm.adapter.out.team.persistence.repository.OrderItemRepository
import team.mozu.dsm.adapter.out.team.persistence.repository.TeamRepository
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.out.team.OrderItemCommandPort
import team.mozu.dsm.domain.team.model.OrderItem
import team.mozu.dsm.domain.team.model.Team
import java.time.LocalDateTime

@Component
class OrderItemPersistenceAdapter(
    private val orderItemRepository: OrderItemRepository,
    private val itemRepository: ItemRepository,
    private val teamRepository: TeamRepository,
    private val orderItemMapper: OrderItemMapper
) : OrderItemCommandPort {

    override fun saveAll(
        requests: List<CompleteInvestmentRequest>,
        team: Team,
        invCount: Int
    ): List<OrderItem> {
        val orders = requests.map { request ->
            OrderItem(
                id = null,
                itemId = request.itemId,
                teamId = team.id!!,
                itemName = request.itemName,
                orderType = request.orderType,
                orderCount = request.orderCount,
                itemPrice = request.itemPrice,
                totalAmount = request.totalAmount,
                invCount = invCount,
                createdAt = LocalDateTime.now(),
                updatedAt = null
            )
        }

        val entities = orders.map { order ->
            val itemEntity = itemRepository.findById(order.itemId)
                .orElseThrow { ItemNotFoundException }

            val teamEntity = teamRepository.findById(order.teamId)
                .orElseThrow { TeamNotFoundException }

            orderItemMapper.toEntity(order, itemEntity, teamEntity)
        }

        val savedEntities = orderItemRepository.saveAll(entities)

        return savedEntities.map { orderItemMapper.toModel(it) }
    }
}
