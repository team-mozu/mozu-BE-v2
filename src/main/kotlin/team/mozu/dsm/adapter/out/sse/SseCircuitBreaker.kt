package team.mozu.dsm.adapter.out.sse

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * SSE 연결에 대한 Circuit Breaker 패턴 구현
 */
@Component
class SseCircuitBreaker {

    private val logger = LoggerFactory.getLogger(SseCircuitBreaker::class.java)

    // 연결별 Circuit Breaker 상태
    private val connectionStates = ConcurrentHashMap<String, CircuitBreakerState>()

    // 전역 Circuit Breaker 상태
    private val globalFailureCount = AtomicInteger(0)
    private val globalLastFailureTime = AtomicLong(0)

    companion object {
        private const val FAILURE_THRESHOLD = 5 // 5번 실패 시 Circuit Open
        private const val TIMEOUT_DURATION_MS = 60000L // 1분 후 Half-Open 시도
        private const val GLOBAL_FAILURE_THRESHOLD = 20 // 전역 실패 임계값
        private const val GLOBAL_TIMEOUT_DURATION_MS = 300000L // 5분 후 전역 복구 시도
    }

    /**
     * 특정 연결에 대해 이벤트 전송을 시도합니다.
     * @param connectionId 연결 ID
     * @param operation 실행할 작업
     * @return 작업 성공 여부
     */
    fun <T> execute(connectionId: String, operation: () -> T): CircuitBreakerResult<T> {
        // 전역 Circuit Breaker 확인
        if (isGlobalCircuitOpen()) {
            logger.debug("Global circuit breaker is open, rejecting operation for connection: {}", connectionId)
            return CircuitBreakerResult.Rejected("Global circuit breaker is open")
        }

        val state = connectionStates.computeIfAbsent(connectionId) { CircuitBreakerState() }

        // 연결별 Circuit Breaker 확인
        when (state.status) {
            CircuitStatus.OPEN -> {
                if (shouldAttemptReset(state)) {
                    state.status = CircuitStatus.HALF_OPEN
                    logger.debug("Circuit breaker for connection {} moved to HALF_OPEN", connectionId)
                } else {
                    logger.debug("Circuit breaker is open for connection: {}", connectionId)
                    return CircuitBreakerResult.Rejected("Circuit breaker is open")
                }
            }
            CircuitStatus.HALF_OPEN -> {
                // Half-Open 상태에서는 하나의 요청만 허용
                if (state.halfOpenAttempts.get() > 0) {
                    logger.debug("Half-open circuit already has pending request for connection: {}", connectionId)
                    return CircuitBreakerResult.Rejected("Half-open circuit busy")
                }
                state.halfOpenAttempts.incrementAndGet()
            }
            CircuitStatus.CLOSED -> {
                // 정상 상태, 작업 진행
            }
        }

        return try {
            val result = operation()
            onSuccess(connectionId, state)
            CircuitBreakerResult.Success(result)
        } catch (e: Exception) {
            onFailure(connectionId, state, e)
            CircuitBreakerResult.Failed(e)
        }
    }

    /**
     * 연결의 Circuit Breaker 상태를 조회합니다.
     */
    fun getConnectionStatus(connectionId: String): CircuitStatus {
        return connectionStates[connectionId]?.status ?: CircuitStatus.CLOSED
    }

    /**
     * 연결의 Circuit Breaker를 리셋합니다.
     */
    fun resetConnection(connectionId: String) {
        connectionStates.remove(connectionId)
        logger.debug("Circuit breaker reset for connection: {}", connectionId)
    }

    /**
     * 전역 Circuit Breaker 상태를 조회합니다.
     */
    fun getGlobalStatus(): CircuitStatus {
        return if (isGlobalCircuitOpen()) CircuitStatus.OPEN else CircuitStatus.CLOSED
    }

    /**
     * 전역 Circuit Breaker를 리셋합니다.
     */
    fun resetGlobal() {
        globalFailureCount.set(0)
        globalLastFailureTime.set(0)
        logger.info("Global circuit breaker reset")
    }

    private fun onSuccess(connectionId: String, state: CircuitBreakerState) {
        when (state.status) {
            CircuitStatus.HALF_OPEN -> {
                state.status = CircuitStatus.CLOSED
                state.failureCount.set(0)
                state.halfOpenAttempts.set(0)
                logger.info("Circuit breaker for connection {} recovered to CLOSED", connectionId)
            }
            CircuitStatus.CLOSED -> {
                // 성공 시 실패 카운트 감소 (점진적 복구)
                if (state.failureCount.get() > 0) {
                    state.failureCount.decrementAndGet()
                }
            }
            CircuitStatus.OPEN -> {
                // Open 상태에서는 성공해도 상태 변경 없음
            }
        }
    }

    private fun onFailure(connectionId: String, state: CircuitBreakerState, exception: Exception) {
        val failures = state.failureCount.incrementAndGet()
        state.lastFailureTime = System.currentTimeMillis()

        // 전역 실패 카운트 증가
        globalFailureCount.incrementAndGet()
        globalLastFailureTime.set(System.currentTimeMillis())

        when (state.status) {
            CircuitStatus.HALF_OPEN -> {
                state.status = CircuitStatus.OPEN
                state.halfOpenAttempts.set(0)
                logger.warn("Circuit breaker for connection {} moved back to OPEN after half-open failure", connectionId)
            }
            CircuitStatus.CLOSED -> {
                if (failures >= FAILURE_THRESHOLD) {
                    state.status = CircuitStatus.OPEN
                    logger.warn("Circuit breaker for connection {} moved to OPEN after {} failures", connectionId, failures)
                }
            }
            CircuitStatus.OPEN -> {
                // 이미 Open 상태
            }
        }

        logger.debug(
            "Circuit breaker failure recorded for connection {}: {} (total: {})",
            connectionId,
            exception.message,
            failures
        )
    }

    private fun shouldAttemptReset(state: CircuitBreakerState): Boolean {
        return System.currentTimeMillis() - state.lastFailureTime >= TIMEOUT_DURATION_MS
    }

    private fun isGlobalCircuitOpen(): Boolean {
        val failures = globalFailureCount.get()
        if (failures < GLOBAL_FAILURE_THRESHOLD) {
            return false
        }

        val timeSinceLastFailure = System.currentTimeMillis() - globalLastFailureTime.get()
        return timeSinceLastFailure < GLOBAL_TIMEOUT_DURATION_MS
    }

    /**
     * Circuit Breaker 상태 정보
     */
    private data class CircuitBreakerState(
        var status: CircuitStatus = CircuitStatus.CLOSED,
        val failureCount: AtomicInteger = AtomicInteger(0),
        var lastFailureTime: Long = 0,
        val halfOpenAttempts: AtomicInteger = AtomicInteger(0)
    )
}

/**
 * Circuit Breaker 상태
 */
enum class CircuitStatus {
    CLOSED, // 정상 상태
    OPEN, // 차단 상태
    HALF_OPEN // 복구 시도 상태
}

/**
 * Circuit Breaker 실행 결과
 */
sealed class CircuitBreakerResult<out T> {
    data class Success<T>(val value: T) : CircuitBreakerResult<T>()
    data class Failed(val exception: Exception) : CircuitBreakerResult<Nothing>()
    data class Rejected(val reason: String) : CircuitBreakerResult<Nothing>()
}
