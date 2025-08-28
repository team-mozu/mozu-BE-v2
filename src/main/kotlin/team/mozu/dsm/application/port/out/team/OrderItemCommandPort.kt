package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.adapter.`in`.team.dto.request.CompleteInvestmentRequest
import team.mozu.dsm.domain.team.model.OrderItem
import team.mozu.dsm.domain.team.model.Team

interface OrderItemCommandPort {

    fun saveAll(requests: List<CompleteInvestmentRequest>, team: Team, invCount: Int): List<OrderItem>
}
