package team.mozu.dsm.adapter.out.sse

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import team.mozu.dsm.application.port.out.sse.SseRateLimitService
import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import java.lang.management.OperatingSystemMXBean

/**
 * 시스템 부하를 모니터링하고 동적으로 Rate Limit을 조정하는 컴포넌트
 */
@Component
class SseSystemLoadMonitor(
    private val sseRateLimitService: SseRateLimitService
) {

    private val logger = LoggerFactory.getLogger(SseSystemLoadMonitor::class.java)

    private val osBean: OperatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean()
    private val memoryBean: MemoryMXBean = ManagementFactory.getMemoryMXBean()

    /**
     * 5분마다 시스템 부하를 확인하고 Rate Limit을 조정합니다.
     */
    @Scheduled(fixedRate = 300_000) // 5분
    fun monitorAndAdjustLimits() {
        try {
            val systemLoad = calculateSystemLoad()

            logger.debug("Current system load: {}", systemLoad)

            // Rate Limit 동적 조정
            sseRateLimitService.adjustLimitsBasedOnLoad(systemLoad)

            // 높은 부하 시 경고 로깅
            if (systemLoad > 0.8) {
                logger.warn("High system load detected: {}. Rate limits have been adjusted.", systemLoad)
            }
        } catch (e: Exception) {
            logger.error("Error monitoring system load", e)
        }
    }

    /**
     * 시스템 부하를 계산합니다.
     * CPU 사용률, 메모리 사용률, SSE 연결 수를 종합적으로 고려합니다.
     */
    private fun calculateSystemLoad(): Double {
        try {
            // 1. CPU 사용률 (0.0 ~ 1.0)
            val cpuLoad = try {
                // OperatingSystemMXBean에서 CPU 사용률 조회
                val systemLoadAverage = osBean.systemLoadAverage
                if (systemLoadAverage >= 0.0) {
                    // Load average를 CPU 사용률로 변환 (대략적)
                    (systemLoadAverage / Runtime.getRuntime().availableProcessors()).coerceAtMost(1.0)
                } else {
                    0.0
                }
            } catch (e: Exception) {
                0.0
            }

            // 2. 메모리 사용률 (0.0 ~ 1.0)
            val memoryUsage = memoryBean.heapMemoryUsage
            val memoryLoad = memoryUsage.used.toDouble() / memoryUsage.max.toDouble()

            // 3. SSE 연결 부하 (0.0 ~ 1.0)
            val totalConnections = sseRateLimitService.getTotalConnectionCount()
            val maxConnections = 1000.0 // 최대 연결 수
            val connectionLoad = (totalConnections / maxConnections).coerceAtMost(1.0)

            // 가중 평균으로 전체 부하 계산
            val systemLoad = (cpuLoad * 0.4 + memoryLoad * 0.4 + connectionLoad * 0.2)
                .coerceIn(0.0, 1.0)

            logger.debug(
                "System load components - CPU: {}, Memory: {}, Connections: {}/{}, Total: {}",
                String.format("%.2f", cpuLoad),
                String.format("%.2f", memoryLoad),
                totalConnections,
                maxConnections.toInt(),
                String.format("%.2f", systemLoad)
            )

            return systemLoad
        } catch (e: Exception) {
            logger.error("Error calculating system load", e)
            return 0.5 // 기본값 반환
        }
    }

    /**
     * 시스템 상태 정보를 로깅합니다.
     */
    @Scheduled(fixedRate = 600_000) // 10분
    fun logSystemStatus() {
        try {
            val systemLoad = calculateSystemLoad()
            val totalConnections = sseRateLimitService.getTotalConnectionCount()

            val memoryUsage = memoryBean.heapMemoryUsage
            val memoryUsedMB = memoryUsage.used / (1024 * 1024)
            val memoryMaxMB = memoryUsage.max / (1024 * 1024)

            logger.info(
                "SSE System Status - Load: {}, Connections: {}, Memory: {}MB/{}MB",
                String.format("%.2f", systemLoad),
                totalConnections,
                memoryUsedMB,
                memoryMaxMB
            )
        } catch (e: Exception) {
            logger.error("Error logging system status", e)
        }
    }

    /**
     * 긴급 상황 시 모든 Rate Limit을 강화합니다.
     */
    fun emergencyRateLimitActivation() {
        try {
            logger.warn("Emergency rate limit activation triggered")
            sseRateLimitService.adjustLimitsBasedOnLoad(1.0) // 최대 부하로 설정
        } catch (e: Exception) {
            logger.error("Error activating emergency rate limits", e)
        }
    }

    /**
     * Rate Limit을 정상 상태로 복구합니다.
     */
    fun resetRateLimitsToNormal() {
        try {
            logger.info("Resetting rate limits to normal levels")
            sseRateLimitService.adjustLimitsBasedOnLoad(0.3) // 정상 부하로 설정
        } catch (e: Exception) {
            logger.error("Error resetting rate limits", e)
        }
    }
}
