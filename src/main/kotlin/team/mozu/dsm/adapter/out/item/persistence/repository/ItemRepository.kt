package team.mozu.dsm.adapter.out.item.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.item.entity.ItemJpaEntity

interface ItemRepository : JpaRepository<ItemJpaEntity, Int>
