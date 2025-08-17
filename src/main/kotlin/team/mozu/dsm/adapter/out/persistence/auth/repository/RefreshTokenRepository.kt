package team.mozu.dsm.adapter.out.persistence.auth.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import team.mozu.dsm.adapter.out.auth.entity.RefreshTokenRedisEntity

@Repository
interface RefreshTokenRepository : CrudRepository<RefreshTokenRedisEntity, String>
