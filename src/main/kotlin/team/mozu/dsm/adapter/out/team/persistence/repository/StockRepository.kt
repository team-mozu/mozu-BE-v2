package team.mozu.dsm.adapter.out.team.persistence.repository

import org.springframework.data.repository.CrudRepository
import team.mozu.dsm.adapter.out.team.entity.StockJpaEntity
import java.util.UUID

interface StockRepository : CrudRepository<StockJpaEntity, UUID> {

    fun findByTeam_IdAndItem_Id(teamId: UUID, itemId: UUID): StockJpaEntity?

    fun findAllByTeam_Id(teamId: UUID): List<StockJpaEntity>
}
