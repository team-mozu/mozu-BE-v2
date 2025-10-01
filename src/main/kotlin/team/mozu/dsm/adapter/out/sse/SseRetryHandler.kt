package team.mozu.dsm.adapter.out.sse

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import team.mozu.dsm.domain.sse.exception.SseException
import kotlin.random.Random

/**
 * SSE 이벤트 전송 실패 시 재시도를 처리하는 클래스
 */
@Component
class SseRetryHandler {

    private val logger = LoggerFactory.getLogger(SseRetryHandler::class.java)

    companion object {
        const val MAX_RETRY_ATTEMPTS = 3
        const val BASE_DELAY_MS = 1000L // 1초
        const val MAX_DELAY_MS = 10000L // 10초
        const val JITTER_RANGE = 0.1 // 10% 지터
    }

    /**
     * 재시도 가능한 작업을 실행합니다.
     * @param operation 실행할 작업
     * @param context 재시도 컨텍스트
     * @return 작업 결과
     */
    fun <T> executeWithRetry(
        operation: () -> T,
        context: RetryContext = RetryContext()
    ): RetryResult<T> {
        var lastException: Exception? = null

        for (attempt in 1..context.maxAttempts) {
            try {
                logger.debug("Executing operation, attempt {}/{}", attempt, context.maxAttempts)
                val result = operation()

                if (attempt > 1) {
                    logger.info("Operation succeeded after {} attempts", attempt)
                }

                return RetryResult.Success(result, attempt)
            } catch (e: Exception) {
                lastException = e

                logger.debug("Operation failed on attempt {}/{}: {}", attempt, context.maxAttempts, e.message)

                // 재시도 불가능한 예외인지 확인
                if (!isRetryableException(e)) {
                    logger.debug("Non-retryable exception encountered: {}", e.javaClass.simpleName)
                    return RetryResult.Failed(e, attempt, "Non-retryable exception")
                }

                // 마지막 시도가 아니면 대기
                if (attempt < context.maxAttempts) {
                    val delay = calculateDelay(attempt, context)
                    logger.debug("Waiting {}ms before retry attempt {}", delay, attempt + 1)

                    try {
                        Thread.sleep(delay)
                    } catch (ie: InterruptedException) {
                        Thread.currentThread().interrupt()
                        logger.warn("Retry interrupted")
                        return RetryResult.Failed(ie, attempt, "Retry interrupted")
                    }
                }
            }
        }

        logger.warn("Operation failed after {} attempts", context.maxAttempts)
        return RetryResult.Failed(
            lastException ?: RuntimeException("Unknown error"),
            context.maxAttempts,
            "Max attempts exceeded"
        )
    }

    /**
     * 예외가 재시도 가능한지 확인합니다.
     */
    private fun isRetryableException(exception: Exception): Boolean {
        return when (exception) {
            // 재시도 불가능한 예외들
            is SseException.UnauthorizedConnectionException -> false
            is SseException.ConnectionLimitExceededException -> false
            is IllegalArgumentException -> false
            is SecurityException -> false

            // 재시도 가능한 예외들
            is SseException.ConnectionClosedException -> true
            is SseException.EventSendFailedException -> true
            is SseException.InvalidConnectionStateException -> true
            is java.io.IOException -> true
            is java.net.SocketException -> true
            is java.util.concurrent.TimeoutException -> true

            // 기본적으로 재시도 가능
            else -> true
        }
    }

    /**
     * 재시도 지연 시간을 계산합니다 (Exponential Backoff with Jitter).
     */
    private fun calculateDelay(attempt: Int, context: RetryContext): Long {
        val exponentialDelay = context.baseDelayMs * (1L shl (attempt - 1)) // 2^(attempt-1)
        val cappedDelay = minOf(exponentialDelay, context.maxDelayMs)

        // 지터 추가 (랜덤 요소로 동시 재시도 방지)
        val jitter = cappedDelay * context.jitterRange * (Random.nextDouble() - 0.5) * 2
        val finalDelay = (cappedDelay + jitter).toLong()

        return maxOf(finalDelay, 0L)
    }
}

/**
 * 재시도 컨텍스트 설정
 */
data class RetryContext(
    val maxAttempts: Int = SseRetryHandler.MAX_RETRY_ATTEMPTS,
    val baseDelayMs: Long = SseRetryHandler.BASE_DELAY_MS,
    val maxDelayMs: Long = SseRetryHandler.MAX_DELAY_MS,
    val jitterRange: Double = SseRetryHandler.JITTER_RANGE
)

/**
 * 재시도 실행 결과
 */
sealed class RetryResult<out T> {
    data class Success<T>(val value: T, val attempts: Int) : RetryResult<T>()
    data class Failed(val exception: Exception, val attempts: Int, val reason: String) : RetryResult<Nothing>()
}
