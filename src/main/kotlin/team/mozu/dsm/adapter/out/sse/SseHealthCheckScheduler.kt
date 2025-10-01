package team.mozu.dsm.adapter.out.sse

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import team.mozu.dsm.application.port.out.sse.SseConnectionManager

/**
 * SSE 연결 헬스체크를 주기적으로 수행하는 스케줄러
 */
@Component
class SseHealthCheckScheduler(
    private val sseConnectionManager: SseConnectionManager
) {

    private val logger = LoggerFactory.getLogger(SseHealthCheckScheduler::class.java)

    /**
     * 5분마다 모든 연결에 대해 헬스체크를 수행합니다.
     */
    @Scheduled(fixedRate = 300000) // 5분 = 300,000ms
    fun performPeriodicHealthCheck() {
        try {
            logger.debug("Starting periodic health check")

            val results = sseConnectionManager.performHealthCheckAll()
            val healthyCount = results.values.count { it }
            val totalCount = results.size

            if (totalCount > 0) {
                logger.info("Periodic health check completed: {}/{} connections healthy", healthyCount, totalCount)

                if (healthyCount < totalCount) {
                    logger.warn("Found {} unhealthy connections during health check", totalCount - healthyCount)
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to perform periodic health check", e)
        }
    }

    /**
     * 10분마다 비정상 연결을 감지하고 정리합니다.
     */
    @Scheduled(fixedRate = 600000) // 10분 = 600,000ms
    fun performPeriodicCleanup() {
        try {
            logger.debug("Starting periodic connection cleanup")

            val cleanedCount = sseConnectionManager.detectAndCleanupAbnormalConnections()

            if (cleanedCount > 0) {
                logger.info("Periodic cleanup completed: {} abnormal connections cleaned", cleanedCount)
            } else {
                logger.debug("Periodic cleanup completed: no abnormal connections found")
            }
        } catch (e: Exception) {
            logger.error("Failed to perform periodic cleanup", e)
        }
    }

    /**
     * 1시간마다 연결 통계를 로깅합니다.
     */
    @Scheduled(fixedRate = 3600000) // 1시간 = 3,600,000ms
    fun logConnectionStats() {
        try {
            val stats = sseConnectionManager.getConnectionStats()

            logger.info(
                "SSE Connection Statistics - Total: {}, Team: {}, Admin: {}, Active: {}, Dead: {}",
                stats.totalConnections,
                stats.teamConnections,
                stats.adminConnections,
                stats.activeConnections,
                stats.deadConnections
            )

            if (stats.deadConnections > 0) {
                logger.warn("Found {} dead connections in statistics", stats.deadConnections)
            }
        } catch (e: Exception) {
            logger.error("Failed to log connection statistics", e)
        }
    }
}
