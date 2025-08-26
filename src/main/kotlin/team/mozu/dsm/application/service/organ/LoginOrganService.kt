package team.mozu.dsm.application.service.organ

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.auth.dto.response.TokenResponse
import team.mozu.dsm.adapter.`in`.organ.dto.request.LoginOrganRequest
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.`in`.organ.LoginOrganUseCase
import team.mozu.dsm.application.port.out.auth.JwtPort
import team.mozu.dsm.application.port.out.organ.QueryOrganPort
import team.mozu.dsm.global.exception.PasswordMisMatchException

@Service
class LoginOrganService(
    private val queryOrganPort: QueryOrganPort,
    private val jwtPort: JwtPort,
    private val passwordEncoder: PasswordEncoder
) : LoginOrganUseCase {

    override fun login(request: LoginOrganRequest): TokenResponse {
        val organ = queryOrganPort.findByOrganCode(request.code)
            ?: throw OrganNotFoundException

        if (!passwordEncoder.matches(request.password, organ.password)) {
            throw PasswordMisMatchException
        }

        return jwtPort.createToken(request.code)
    }
}
