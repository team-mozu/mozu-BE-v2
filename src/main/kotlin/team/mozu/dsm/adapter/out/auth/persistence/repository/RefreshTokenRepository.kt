package team.mozu.dsm.adapter.out.auth.persistence.repository

import org.springframework.data.repository.CrudRepository
import team.mozu.dsm.adapter.out.auth.entity.RefreshTokenRedisEntity

interface RefreshTokenRepository : CrudRepository<RefreshTokenRedisEntity, String> {

    fun findByRefreshToken(refreshToken: String): RefreshTokenRedisEntity?

    fun deleteByRefreshToken(refreshToken: String): RefreshTokenRedisEntity?
}
