package team.mozu.dsm.adapter.out.auth.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.auth.entity.RefreshTokenRedisEntity
import team.mozu.dsm.adapter.out.auth.persistence.repository.RefreshTokenRepository
import team.mozu.dsm.application.port.out.auth.CommandTokenPort
import team.mozu.dsm.application.port.out.auth.QueryTokenPort

@Component
class RefreshTokenPersistenceAdapter(
    private val refreshTokenRepository: RefreshTokenRepository
) : QueryTokenPort, CommandTokenPort {

    override fun findByRefreshToken(refreshToken: String): RefreshTokenRedisEntity? {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
    }

    override fun deleteToken(refreshToken: String): RefreshTokenRedisEntity? {
        return refreshTokenRepository.deleteToken(refreshToken)
    }
}
