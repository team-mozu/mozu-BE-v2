package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.adapter.`in`.team.dto.response.OrderItemResponse
import team.mozu.dsm.domain.team.model.OrderItem
import java.util.UUID

interface QueryOrderItemPort {

    fun findAllByTeamId(teamId: UUID): List<OrderItem>

    fun findByTeamId(teamId: UUID): List<OrderItemResponse>
}
