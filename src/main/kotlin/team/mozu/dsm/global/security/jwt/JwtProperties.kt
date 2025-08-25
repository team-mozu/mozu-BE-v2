package team.mozu.dsm.global.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
class JwtProperties(
    val header: String,
    val prefix: String,
    val secretKey: String,
    val accessExp: Long,
    val refreshExp: Long,
    /**
     * 학생용 AccessToken 만료 시간 (6시간)
     * 기관용과는 달리 수업 진행 시간 동안 유지되어야 하므로 별도로 관리
     */
    val studentAccessExp: Long
)
