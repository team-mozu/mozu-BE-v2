package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.response.OrderItemResponse
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.GetCurrentOrderItemUseCase
import team.mozu.dsm.application.port.out.team.QueryOrderItemPort
import java.util.*

@Service
class GetCurrentOrderItemService(
    private val queryOrderItemPort: QueryOrderItemPort
) : GetCurrentOrderItemUseCase {

    @Transactional(readOnly = true)
    override fun getCurrentOrderItem(teamId: UUID): List<OrderItemResponse> {
        val orderItems = queryOrderItemPort.findAllByTeamId(teamId)
            ?: throw TeamNotFoundException

        return orderItems.map { orderItem ->
            OrderItemResponse(
                id = orderItem.id!!,
                itemId = orderItem.itemId,
                itemName = orderItem.itemName,
                itemPrice = orderItem.itemPrice,
                orderCount = orderItem.orderCount,
                totalMoney = orderItem.totalMoney,
                orderType = orderItem.orderType,
                invCount = orderItem.invCount
            )
        }
    }
}
