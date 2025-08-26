package team.mozu.dsm.application.port.out.auth

import team.mozu.dsm.adapter.out.auth.entity.RefreshTokenRedisEntity

interface QueryTokenPort {

    fun findByToken(token: String): RefreshTokenRedisEntity?
}
