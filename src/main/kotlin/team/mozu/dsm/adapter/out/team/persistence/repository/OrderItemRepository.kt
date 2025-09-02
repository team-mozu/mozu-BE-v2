package team.mozu.dsm.adapter.out.team.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.team.entity.OrderItemJpaEntity
import java.util.UUID

interface OrderItemRepository : JpaRepository<OrderItemJpaEntity, UUID> {

    fun findAllByTeamId(teamId: UUID): List<OrderItemJpaEntity>
}
