package team.mozu.dsm.global.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
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
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        configuration.setAllowedOrigins(
            mutableListOf<String?>(
                "http://localhost:3000",
                "https://mozu-v2-prod.dsmhs.kr",
                "https://mozu-v2-stag.dsmhs.kr"
            )
        )
        configuration.setAllowedMethods(mutableListOf<String?>("GET", "POST", "PUT", "DELETE", "OPTIONS"))
        configuration.setAllowedHeaders(mutableListOf<String?>("Authorization", "Content-Type"))
        configuration.setAllowCredentials(true)

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }


//
//    @Bean
//    fun configure(http: HttpSecurity): SecurityFilterChain {
//        http.csrf(AbstractHttpConfigurer<*, *>::disable)
//            .cors { it.configurationSource(corsConfigurationSource()) }
//            .headers { headers: HeadersConfigurer<HttpSecurity> ->
//                headers.frameOptions { frame -> frame.sameOrigin() }
//            }
//            .sessionManagement {
//                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            }
//            .authorizeHttpRequests {
//                // organ
//                it.requestMatchers(HttpMethod.POST, "/organ/create").permitAll()
//                it.requestMatchers(HttpMethod.PATCH, "/organ/token/reissue").permitAll()
//                it.requestMatchers(HttpMethod.POST, "/organ/login").permitAll()
//                it.requestMatchers(HttpMethod.GET, "/organ/*").permitAll()
//                it.requestMatchers(HttpMethod.GET, "/organ").permitAll()
//
//                // team
//                it.requestMatchers(HttpMethod.POST, "/team/participate").permitAll()
//                it.requestMatchers(HttpMethod.POST, "/team/end").hasRole("STUDENT")
//                it.requestMatchers(HttpMethod.GET, "/team/stocks").hasRole("STUDENT")
//                it.requestMatchers(HttpMethod.GET, "/team/detail").hasRole("STUDENT")
//                it.requestMatchers(HttpMethod.GET, "/team/orders").hasRole("STUDENT")
//                it.requestMatchers(HttpMethod.GET, "/team/result").hasRole("STUDENT")
//                it.requestMatchers(HttpMethod.GET, "/team/ranks").hasRole("STUDENT")
//                it.requestMatchers(HttpMethod.GET, "/team/sse").hasRole("STUDENT")
//
//                // lesson
//                it.requestMatchers(HttpMethod.GET, "/lesson/team/items").hasRole("STUDENT")
//                it.requestMatchers(HttpMethod.GET, "/lesson/team/articles").hasRole("STUDENT")
//                it.requestMatchers(HttpMethod.GET, "/lesson/team/item/*").hasRole("STUDENT")
//                    .anyRequest().authenticated()
//            }
//            .with(FilterConfig(jwtTokenProvider, objectMapper), Customizer.withDefaults())
//
//        return http.build()
//    }
//
//    @Bean
//    fun corsConfigurationSource(): CorsConfigurationSource {
//        val configuration = CorsConfiguration()
//        configuration.allowedOrigins = listOf(
//            "http://localhost:3000",
//            "https://mozu-v2-prod.dsmhs.kr",
//            "https://mozu-v2-stag.dsmhs.kr"
//        )
//        configuration.allowedMethods = listOf(
//            HttpMethod.GET.name(),
//            HttpMethod.POST.name(),
//            HttpMethod.PUT.name(),
//            HttpMethod.PATCH.name(),
//            HttpMethod.DELETE.name()
//        )
//        configuration.allowedHeaders = listOf("Content-Type", "Authorization")
//        configuration.allowCredentials = true
//
//        val source = UrlBasedCorsConfigurationSource()
//        source.registerCorsConfiguration("/**", configuration)
//        return source
//    }
}
