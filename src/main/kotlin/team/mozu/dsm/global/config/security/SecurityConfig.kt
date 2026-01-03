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
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // organ
                    .requestMatchers(HttpMethod.POST, "/organ/create").permitAll()
                    .requestMatchers(HttpMethod.PATCH, "/organ/token/reissue").permitAll()
                    .requestMatchers(HttpMethod.POST, "/organ/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/organ/my").authenticated()
                    .requestMatchers(HttpMethod.GET, "/organ/*").permitAll()
                    .requestMatchers(HttpMethod.GET, "/organ").permitAll()
                    // team
                    .requestMatchers(HttpMethod.POST, "/team/participate").permitAll()
                    .requestMatchers(HttpMethod.POST, "/team/end").hasRole("STUDENT")
                    .requestMatchers(HttpMethod.GET, "/team/stocks").hasRole("STUDENT")
                    .requestMatchers(HttpMethod.GET, "/team/detail").hasRole("STUDENT")
                    .requestMatchers(HttpMethod.GET, "/team/orders").hasRole("STUDENT")
                    .requestMatchers(HttpMethod.GET, "/team/result").hasRole("STUDENT")
                    .requestMatchers(HttpMethod.GET, "/team/ranks").hasRole("STUDENT")
                    .requestMatchers(HttpMethod.GET, "/team/sse").hasRole("STUDENT")
                    .requestMatchers(HttpMethod.GET, "/team/*/holdItems").permitAll()
                    // lesson
                    .requestMatchers(HttpMethod.GET, "/lesson/team/items").hasRole("STUDENT")
                    .requestMatchers(HttpMethod.GET, "/lesson/team/articles").hasRole("STUDENT")
                    .requestMatchers(HttpMethod.GET, "/lesson/team/item/*").hasRole("STUDENT")
                    // swagger
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/swagger-resources/**").permitAll()
                    .requestMatchers("/webjars/**").permitAll()
                    // 나머지
                    .anyRequest().authenticated()
            }
            .with(FilterConfig(jwtTokenProvider, objectMapper), Customizer.withDefaults())

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf(
            "http://admin.localhost:3002",
            "http://student.localhost:3001",
            "https://mozu-admin.vercel.app",
            "https://mozu-student.vercel.app"
        )
        configuration.allowedMethods = listOf(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name()
        )
        configuration.allowedHeaders = listOf("Content-Type", "Authorization")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
