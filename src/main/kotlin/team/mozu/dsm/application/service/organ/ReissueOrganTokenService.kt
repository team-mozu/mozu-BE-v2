package team.mozu.dsm.application.service.organ

import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.auth.dto.response.TokenResponse
import team.mozu.dsm.adapter.`in`.organ.dto.request.ReissueOrganTokenRequest
import team.mozu.dsm.application.port.`in`.organ.ReissueOrganTokenUseCase
import team.mozu.dsm.application.port.out.auth.CommandTokenPort
import team.mozu.dsm.application.port.out.auth.JwtPort
import team.mozu.dsm.application.port.out.auth.QueryTokenPort
import team.mozu.dsm.global.exception.RefreshTokenNotFoundException

@Service
class ReissueOrganTokenService(
    private val jwtPort: JwtPort,
    private val queryTokenPort: QueryTokenPort,
    private val commandTokenPort: CommandTokenPort
) : ReissueOrganTokenUseCase {

    override fun reissue(request: ReissueOrganTokenRequest): TokenResponse {
        val refreshToken = queryTokenPort.findByRefreshToken(request.refreshToken)
            ?: throw RefreshTokenNotFoundException

        commandTokenPort.deleteToken(request.refreshToken)

        return jwtPort.createToken(refreshToken.organCode)
    }
}
