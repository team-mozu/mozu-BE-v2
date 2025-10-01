package team.mozu.dsm.adapter.out.sse

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import team.mozu.dsm.application.port.out.sse.SseConnectionManager
import team.mozu.dsm.application.port.out.sse.SseRateLimitService
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@Service
class SseRateLimitServiceImpl(
    private val redisTemplate: RedisTemplate<String, String>,
    private val sseConnectionManager: SseConnectionManager
) : SseRateLimitService {

    private val logger = LoggerFactory.getLogger(SseRateLimitServiceImpl::class.java)

    companion object {
        // Rate Limit 설정
        private const val MAX_CONNECTIONS_PER_CLIENT = 3 // 클라이언트당 최대 연결 수
        private const val MAX_CONNECTIONS_PER_TEAM = 10 // 팀당 최대 연결 수
        private const val MAX_ADMIN_CONNECTIONS_PER_LESSON = 5 // 수업당 최대 관리자 연결 수
        private const val MAX_TOTAL_CONNECTIONS = 1000 // 전체 최대 연결 수

        private const val CONNECTION_ATTEMPTS_PER_MINUTE = 10 // 분당 연결 시도 제한
        private const val EVENT_SENDS_PER_MINUTE = 60 // 분당 이벤트 전송 제한

        // Redis 키 패턴
        private const val CONNECTION_ATTEMPTS_KEY = "sse:rate_limit:connection:{clientId}"
        private const val EVENT_SENDS_KEY = "sse:rate_limit:events:{clientId}"
        private const val CLIENT_CONNECTIONS_KEY = "sse:connections:client:{clientId}"
        private const val SYSTEM_LOAD_KEY = "sse:system:load"

        // TTL 설정
        private val RATE_LIMIT_TTL = Duration.ofMinutes(1)
        private val CONNECTION_TRACKING_TTL = Duration.ofHours(1)
    }

    // 동적 제한 조정을 위한 변수들
    @Volatile
    private var dynamicConnectionLimit = MAX_CONNECTIONS_PER_CLIENT

    @Volatile
    private var dynamicEventLimit = EVENT_SENDS_PER_MINUTE

    override fun allowConnection(clientId: String, resourceType: String): Boolean {
        try {
            // 1. 클라이언트별 연결 시도 횟수 확인
            val connectionAttemptsKey = CONNECTION_ATTEMPTS_KEY.replace("{clientId}", clientId)
            val attempts = redisTemplate.opsForValue().get(connectionAttemptsKey)?.toIntOrNull() ?: 0

            if (attempts >= CONNECTION_ATTEMPTS_PER_MINUTE) {
                logRateLimitViolation(clientId, resourceType, "Connection attempts per minute exceeded: $attempts")
                return false
            }

            // 2. 클라이언트별 동시 연결 수 확인
            val clientConnectionsKey = CLIENT_CONNECTIONS_KEY.replace("{clientId}", clientId)
            val currentConnections = redisTemplate.opsForSet().size(clientConnectionsKey) ?: 0

            if (currentConnections >= dynamicConnectionLimit) {
                logRateLimitViolation(clientId, resourceType, "Max connections per client exceeded: $currentConnections")
                return false
            }

            // 3. 전체 시스템 연결 수 확인
            val totalConnections = getTotalConnectionCount()
            if (totalConnections >= MAX_TOTAL_CONNECTIONS) {
                logRateLimitViolation(clientId, resourceType, "Max total connections exceeded: $totalConnections")
                return false
            }

            // 4. 리소스별 추가 검증
            when (resourceType) {
                "TEAM_SSE" -> {
                    // 팀별 연결 수는 실제 연결 시 teamId로 확인
                }
                "ADMIN_SSE" -> {
                    // 관리자 연결은 실제 연결 시 lessonId로 확인
                }
            }

            return true
        } catch (e: Exception) {
            logger.error("Error checking connection rate limit for client: $clientId", e)
            // 에러 발생 시 안전하게 허용 (서비스 가용성 우선)
            return true
        }
    }

    override fun allowEventSend(clientId: String, eventType: String): Boolean {
        try {
            val eventSendsKey = EVENT_SENDS_KEY.replace("{clientId}", clientId)
            val sends = redisTemplate.opsForValue().get(eventSendsKey)?.toIntOrNull() ?: 0

            if (sends >= dynamicEventLimit) {
                logRateLimitViolation(clientId, "EVENT_SEND", "Event sends per minute exceeded: $sends")
                return false
            }

            return true
        } catch (e: Exception) {
            logger.error("Error checking event send rate limit for client: $clientId", e)
            return true
        }
    }

    override fun getTeamConnectionCount(teamId: UUID): Int {
        return try {
            sseConnectionManager.getTeamConnections(teamId).size
        } catch (e: Exception) {
            logger.error("Error getting team connection count for team: $teamId", e)
            0
        }
    }

    override fun getAdminConnectionCount(lessonId: UUID): Int {
        return try {
            sseConnectionManager.getAdminConnections(lessonId).size
        } catch (e: Exception) {
            logger.error("Error getting admin connection count for lesson: $lessonId", e)
            0
        }
    }

    override fun getTotalConnectionCount(): Int {
        return try {
            val stats = sseConnectionManager.getConnectionStats()
            stats.totalConnections
        } catch (e: Exception) {
            logger.error("Error getting total connection count", e)
            0
        }
    }

    override fun recordConnectionAttempt(clientId: String, resourceType: String) {
        try {
            val connectionAttemptsKey = CONNECTION_ATTEMPTS_KEY.replace("{clientId}", clientId)

            // 현재 시도 횟수 증가
            val newCount = redisTemplate.opsForValue().increment(connectionAttemptsKey) ?: 1

            // 첫 번째 시도인 경우 TTL 설정
            if (newCount == 1L) {
                redisTemplate.expire(connectionAttemptsKey, RATE_LIMIT_TTL.seconds, TimeUnit.SECONDS)
            }

            // 클라이언트 연결 추적
            val clientConnectionsKey = CLIENT_CONNECTIONS_KEY.replace("{clientId}", clientId)
            redisTemplate.opsForSet().add(clientConnectionsKey, System.currentTimeMillis().toString())
            redisTemplate.expire(clientConnectionsKey, CONNECTION_TRACKING_TTL.seconds, TimeUnit.SECONDS)
        } catch (e: Exception) {
            logger.error("Error recording connection attempt for client: $clientId", e)
        }
    }

    override fun recordEventSend(clientId: String, eventType: String) {
        try {
            val eventSendsKey = EVENT_SENDS_KEY.replace("{clientId}", clientId)

            val newCount = redisTemplate.opsForValue().increment(eventSendsKey) ?: 1

            if (newCount == 1L) {
                redisTemplate.expire(eventSendsKey, RATE_LIMIT_TTL.seconds, TimeUnit.SECONDS)
            }
        } catch (e: Exception) {
            logger.error("Error recording event send for client: $clientId", e)
        }
    }

    override fun resetClientLimits(clientId: String) {
        try {
            val connectionAttemptsKey = CONNECTION_ATTEMPTS_KEY.replace("{clientId}", clientId)
            val eventSendsKey = EVENT_SENDS_KEY.replace("{clientId}", clientId)
            val clientConnectionsKey = CLIENT_CONNECTIONS_KEY.replace("{clientId}", clientId)

            redisTemplate.delete(connectionAttemptsKey)
            redisTemplate.delete(eventSendsKey)
            redisTemplate.delete(clientConnectionsKey)

            logger.info("Reset rate limits for client: $clientId")
        } catch (e: Exception) {
            logger.error("Error resetting client limits for client: $clientId", e)
        }
    }

    override fun adjustLimitsBasedOnLoad(systemLoad: Double) {
        try {
            // 시스템 부하 저장
            redisTemplate.opsForValue().set(SYSTEM_LOAD_KEY, systemLoad.toString())

            // 부하에 따른 동적 제한 조정
            when {
                systemLoad > 0.9 -> {
                    // 매우 높은 부하: 제한 강화
                    dynamicConnectionLimit = (MAX_CONNECTIONS_PER_CLIENT * 0.5).toInt()
                    dynamicEventLimit = (EVENT_SENDS_PER_MINUTE * 0.5).toInt()
                    logger.warn("High system load detected ($systemLoad), tightening rate limits")
                }
                systemLoad > 0.7 -> {
                    // 높은 부하: 제한 약간 강화
                    dynamicConnectionLimit = (MAX_CONNECTIONS_PER_CLIENT * 0.7).toInt()
                    dynamicEventLimit = (EVENT_SENDS_PER_MINUTE * 0.7).toInt()
                    logger.info("Moderate system load detected ($systemLoad), adjusting rate limits")
                }
                else -> {
                    // 정상 부하: 기본 제한
                    dynamicConnectionLimit = MAX_CONNECTIONS_PER_CLIENT
                    dynamicEventLimit = EVENT_SENDS_PER_MINUTE
                }
            }
        } catch (e: Exception) {
            logger.error("Error adjusting limits based on system load: $systemLoad", e)
        }
    }

    override fun logRateLimitViolation(clientId: String, resourceType: String, reason: String) {
        logger.warn(
            "Rate limit violation - Client: {}, Resource: {}, Reason: {}, Timestamp: {}",
            clientId,
            resourceType,
            reason,
            LocalDateTime.now()
        )

        // 보안 이벤트 로깅
        logger.info(
            "Security Event - Type: RATE_LIMIT_VIOLATION, Client: {}, Resource: {}, Reason: {}, Timestamp: {}",
            clientId,
            resourceType,
            reason,
            System.currentTimeMillis()
        )
    }

    /**
     * 팀별 연결 수 제한 검증
     */
    fun validateTeamConnectionLimit(teamId: UUID): Boolean {
        val currentConnections = getTeamConnectionCount(teamId)
        return currentConnections < MAX_CONNECTIONS_PER_TEAM
    }

    /**
     * 관리자 연결 수 제한 검증
     */
    fun validateAdminConnectionLimit(lessonId: UUID): Boolean {
        val currentConnections = getAdminConnectionCount(lessonId)
        return currentConnections < MAX_ADMIN_CONNECTIONS_PER_LESSON
    }
}
