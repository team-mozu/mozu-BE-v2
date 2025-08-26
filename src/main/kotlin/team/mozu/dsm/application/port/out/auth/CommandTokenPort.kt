package team.mozu.dsm.application.port.out.auth

import team.mozu.dsm.adapter.out.auth.entity.RefreshTokenRedisEntity

interface CommandTokenPort {

    fun deleteToken(refreshToken: String): RefreshTokenRedisEntity?
}
