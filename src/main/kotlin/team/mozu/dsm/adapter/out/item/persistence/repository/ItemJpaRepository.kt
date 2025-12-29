package team.mozu.dsm.adapter.out.item.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.item.entity.ItemJpaEntity
import java.util.UUID

interface ItemJpaRepository : JpaRepository<ItemJpaEntity, Int> {

    fun findByIdAndIsDeletedFalse(id: Int): ItemJpaEntity?

    fun findAllByIdInAndIsDeletedFalse(ids: List<Int>): List<ItemJpaEntity>

    fun findAllByOrganIdAndIsDeletedFalse(organId: UUID): List<ItemJpaEntity>
}
