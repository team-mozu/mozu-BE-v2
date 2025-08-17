package team.mozu.dsm.global.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import team.mozu.dsm.global.security.jwt.JwtTokenProvider
import java.util.*

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun passwordEncorder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun configure(http: HttpSecurity): SecurityFilterChain {
        http.csrf(AbstractHttpConfigurer<*, *>::disable)
            .cors { it.configurationSource(corsConfigurationSource()) }
            .headers { headers: HeadersConfigurer<HttpSecurity> ->
                headers.frameOptions { frame -> frame.sameOrigin() }
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/user/**",
                    "/auth/**"
                ).permitAll()
                    .anyRequest().authenticated()
            }
            .with(FilterConfig(jwtTokenProvider, objectMapper), Customizer.withDefaults())

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = Arrays.asList(
            HttpMethod.OPTIONS.name(),
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name()
        )
        configuration.allowCredentials = false
        configuration.addAllowedHeader("*")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
