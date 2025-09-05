package team.mozu.dsm.adapter.out.team.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.team.entity.TeamJpaEntity
import java.util.UUID

interface TeamRepository : JpaRepository<TeamJpaEntity, UUID>
