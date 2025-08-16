package team.mozu.dsm.adapter.out.persistence.organ.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import java.util.*

interface OrganRepository: JpaRepository<OrganJpaEntity, UUID> {
}
