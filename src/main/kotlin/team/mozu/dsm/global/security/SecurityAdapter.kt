package team.mozu.dsm.global.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.domain.organ.model.Organ
import team.mozu.dsm.global.exception.UnauthenticatedException
import team.mozu.dsm.global.security.auth.CustomUserDetails

@Component
class SecurityAdapter : SecurityPort {

    override fun getCurrentOrgan(): Organ {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw UnauthenticatedException

        val principal = authentication.principal
        if (principal is CustomUserDetails) {
            return principal.organ // 로그인 시 넣어둔 Organ 꺼내기
        } else {
            throw OrganNotFoundException
        }
    }
}
