package team.mozu.dsm.adapter.out.student.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.student.entity.TeamJpaEntity
import java.util.UUID

interface TeamRepository : JpaRepository<TeamJpaEntity, UUID> {
}
