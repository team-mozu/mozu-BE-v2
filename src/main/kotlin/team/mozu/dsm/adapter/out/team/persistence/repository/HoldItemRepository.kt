package team.mozu.dsm.adapter.out.team.persistence.repository

import org.springframework.data.repository.CrudRepository
import team.mozu.dsm.adapter.out.team.entity.HoldItemJpaEntity
import java.util.UUID

interface HoldItemRepository : CrudRepository<HoldItemJpaEntity, UUID>
