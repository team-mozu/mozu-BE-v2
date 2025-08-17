package team.mozu.dsm.global.security.auth

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import team.mozu.dsm.application.port.out.organ.FindOrganPort

@Component
class CustomUserDetailsService(
    private val findOrganPort: FindOrganPort
) : UserDetailsService {

    override fun loadUserByUsername(organCode: String): UserDetails {
        val organ = findOrganPort.findByOrganCode(organCode) ?: throw UsernameNotFoundException("Organ Not Found")
        return CustomUserDetails(organ)
    }
}
