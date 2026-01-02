package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.adapter.`in`.team.dto.response.OrderItemResponse
import java.util.UUID

interface QueryOrderItemsUseCase {

    fun execute(teamId: UUID): List<OrderItemResponse>
}
