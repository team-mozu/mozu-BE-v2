package team.mozu.dsm.adapter.out.item.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ItemRepository : JpaRepository<ItemRepository, UUID>
