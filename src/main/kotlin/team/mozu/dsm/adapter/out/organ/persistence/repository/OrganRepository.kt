package team.mozu.dsm.adapter.out.organ.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import java.util.UUID

interface OrganRepository : JpaRepository<OrganJpaEntity, UUID> {
    fun findByOrganCode(organCode: String): OrganJpaEntity?
}
