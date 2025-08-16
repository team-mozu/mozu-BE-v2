package team.mozu.dsm.adapter.out.persistence.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.auth.entity.RefreshTokenRedisEntity

interface RefreshTokenRepository : JpaRepository<RefreshTokenRedisEntity, String>
