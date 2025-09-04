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
import team.mozu.dsm.global.security.jwt.JwtAdapter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtAdapter,
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
                //organ
                it.requestMatchers(HttpMethod.POST, "/organ/create").permitAll()
                it.requestMatchers(HttpMethod.PATCH, "/organ/token/reissue").permitAll()
                it.requestMatchers(HttpMethod.POST, "/organ/login").permitAll()
                it.requestMatchers(HttpMethod.GET, "/organ/{id}").permitAll()
                it.requestMatchers(HttpMethod.GET, "/organ").permitAll()

                //team
                it.requestMatchers(HttpMethod.POST, "/team/participate").permitAll()
                it.requestMatchers(HttpMethod.POST, "/team/end").hasAnyRole("STUDENT")
                it.requestMatchers(HttpMethod.GET, "/team/stocks").hasAnyRole("STUDENT")
                it.requestMatchers(HttpMethod.GET, "/team/detail").hasAnyRole("STUDENT")
                it.requestMatchers(HttpMethod.GET, "/team/orders").hasAnyRole("STUDENT")
                it.requestMatchers(HttpMethod.GET, "/team/result").hasAnyRole("STUDENT")
                it.requestMatchers(HttpMethod.GET, "/team/rank").hasAnyRole("STUDENT")
                    .anyRequest().authenticated()
            }
            .with(FilterConfig(jwtTokenProvider, objectMapper), Customizer.withDefaults())

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000")
        configuration.allowedMethods = listOf(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name()
        )
        configuration.allowedHeaders = listOf("Content-Type", "Authorization")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
