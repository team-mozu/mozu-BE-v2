package team.mozu.dsm.adapter.out.sse

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import team.mozu.dsm.application.port.out.sse.SseConnectionManager

/**
 * SSE Circuit Breaker 상태를 모니터링하는 컴포넌트
 */
@Component
class SseCircuitBreakerMonitor(
    private val circuitBreaker: SseCircuitBreaker,
    private val sseConnectionManager: SseConnectionManager
) {

    private val logger = LoggerFactory.getLogger(SseCircuitBreakerMonitor::class.java)

    /**
     * 30분마다 Circuit Breaker 상태를 로깅합니다.
     */
    @Scheduled(fixedRate = 1800000) // 30분 = 1,800,000ms
    fun logCircuitBreakerStatus() {
        try {
            val globalStatus = circuitBreaker.getGlobalStatus()

            logger.info("Circuit Breaker Global Status: {}", globalStatus)

            if (globalStatus == CircuitStatus.OPEN) {
                logger.warn("Global circuit breaker is OPEN - SSE system is in degraded mode")
            }

            // 연결별 상태는 디버그 레벨로만 로깅 (너무 많을 수 있음)
            val stats = sseConnectionManager.getConnectionStats()
            logger.info(
                "Circuit Breaker Monitor - Active connections: {}, Total connections: {}",
                stats.activeConnections,
                stats.totalConnections
            )
        } catch (e: Exception) {
            logger.error("Failed to log circuit breaker status", e)
        }
    }

    /**
     * 1시간마다 Circuit Breaker를 부분적으로 리셋합니다.
     */
    @Scheduled(fixedRate = 3600000) // 1시간 = 3,600,000ms
    fun performPeriodicReset() {
        try {
            val globalStatus = circuitBreaker.getGlobalStatus()

            // 전역 Circuit Breaker가 오랫동안 열려있으면 리셋 시도
            if (globalStatus == CircuitStatus.OPEN) {
                logger.info("Attempting to reset global circuit breaker after periodic check")
                circuitBreaker.resetGlobal()
            }

            logger.debug("Periodic circuit breaker reset check completed")
        } catch (e: Exception) {
            logger.error("Failed to perform periodic circuit breaker reset", e)
        }
    }

    /**
     * Circuit Breaker 통계를 수집합니다.
     */
    fun getCircuitBreakerStats(): CircuitBreakerStats {
        return try {
            val globalStatus = circuitBreaker.getGlobalStatus()
            val connectionStats = sseConnectionManager.getConnectionStats()

            CircuitBreakerStats(
                globalStatus = globalStatus,
                totalConnections = connectionStats.totalConnections,
                activeConnections = connectionStats.activeConnections,
                deadConnections = connectionStats.deadConnections
            )
        } catch (e: Exception) {
            logger.error("Failed to get circuit breaker stats", e)
            CircuitBreakerStats(
                globalStatus = CircuitStatus.CLOSED,
                totalConnections = 0,
                activeConnections = 0,
                deadConnections = 0
            )
        }
    }
}

/**
 * Circuit Breaker 통계 정보
 */
data class CircuitBreakerStats(
    val globalStatus: CircuitStatus,
    val totalConnections: Int,
    val activeConnections: Int,
    val deadConnections: Int
)
