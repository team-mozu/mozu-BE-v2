package team.mozu.dsm.infrastructure.config.filter

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import team.mozu.dsm.global.error.GlobalExceptionFilter
import team.mozu.dsm.global.security.jwt.JwtTokenFilter
import team.mozu.dsm.global.security.jwt.JwtTokenProvider

class FilterConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(http: HttpSecurity) {
        val jwtTokenFilter = JwtTokenFilter(jwtTokenProvider)
        val globalExceptionFilter = GlobalExceptionFilter(objectMapper)

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(globalExceptionFilter, JwtTokenFilter::class.java)
    }
}
