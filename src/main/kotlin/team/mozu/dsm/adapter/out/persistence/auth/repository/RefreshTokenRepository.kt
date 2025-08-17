package team.mozu.dsm.adapter.out.persistence.auth.repository

import org.springframework.data.repository.CrudRepository
import team.mozu.dsm.adapter.out.auth.entity.RefreshTokenRedisEntity

interface RefreshTokenRepository : CrudRepository<RefreshTokenRedisEntity, String>
