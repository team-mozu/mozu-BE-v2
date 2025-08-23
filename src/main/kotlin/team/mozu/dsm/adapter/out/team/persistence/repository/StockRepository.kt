package team.mozu.dsm.adapter.out.team.persistence.repository

import org.springframework.data.repository.CrudRepository
import team.mozu.dsm.adapter.out.team.entity.StockJpaEntity
import java.util.UUID

interface StockRepository : CrudRepository<StockJpaEntity, UUID>
