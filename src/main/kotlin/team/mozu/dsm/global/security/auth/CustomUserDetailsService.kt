package team.mozu.dsm.global.security.auth

import com.example.kotlin_practice.global.security.auth.CustomUserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import team.mozu.dsm.application.port.out.FindOrganPort

@Component
class CustomUserDetailsService(
    private val findOrganPort: FindOrganPort
) : UserDetailsService {
    override fun loadUserByUsername(organCode: String): CustomUserDetails {
        val organ = findOrganPort.findByOrganCode(organCode) ?: throw UsernameNotFoundException("Organ Not Found")
        return CustomUserDetails(organ)
    }
}
