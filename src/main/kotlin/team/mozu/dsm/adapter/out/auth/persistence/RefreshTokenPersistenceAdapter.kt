package team.mozu.dsm.adapter.out.auth.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.auth.entity.RefreshTokenRedisEntity
import team.mozu.dsm.adapter.out.auth.persistence.repository.RefreshTokenRepository
import team.mozu.dsm.application.port.out.auth.QueryTokenPort

@Component
class RefreshTokenPersistenceAdapter(
    private val refreshTokenRepository: RefreshTokenRepository
) : QueryTokenPort {

    override fun findByToken(token: String): RefreshTokenRedisEntity? {
        return refreshTokenRepository.findByToken(token)
    }
}
