package team.mozu.dsm.global.error.sse

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.out.sse.persistence.repository.SseEmitterRepository
import team.mozu.dsm.global.exception.sse.InvalidSseDataException
import team.mozu.dsm.global.exception.sse.InvalidSseStateException
import team.mozu.dsm.global.exception.sse.SseConnectionClosedException
import java.io.IOException

@Component
class SseExceptionHandler(
    private val sseEmitterRepository: SseEmitterRepository
) {

    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    fun handle(clientId: String, emitter: SseEmitter, exception: Exception) {
        when (exception) {
            is IOException -> {
                if (exception.message?.contains("Broken pipe") == true || exception.message?.contains("Connection reset") == true) {
                    // 클라이언트가 연결을 끊은 경우에만 emitter 삭제
                    log.info("Client disconnected: clientId=$clientId")
                    runCatching { emitter.completeWithError(exception) }
                        .onFailure { log.error("Failed to complete emitter with error for clientId=$clientId", it) }
                    sseEmitterRepository.delete(clientId)
                    throw SseConnectionClosedException
                } else {
                    // 일시적 오류는 로깅만 하고 연결 유지
                    log.warn("SSE IOException (connection maintained): clientId=$clientId, msg=${exception.message}")
                }
            }
            is IllegalStateException -> {
                if (exception.message?.contains("ResponseBodyEmitter is already set complete") == true) {
                    // emitter가 이미 완료된 경우
                    log.info("Emitter already completed: clientId=$clientId")
                    sseEmitterRepository.delete(clientId)
                    throw InvalidSseStateException
                } else {
                    // 기타 IllegalStateException은 로깅만 하고 연결 유지
                    log.warn("SSE IllegalState (connection maintained): clientId=$clientId, msg=${exception.message}")
                }
            }
            is IllegalArgumentException -> {
                log.error("SSE IllegalArgument: clientId=$clientId, msg=${exception.message}")
                throw InvalidSseDataException
            }
            else -> {
                log.error("Unknown SSE error (connection maintained): clientId=$clientId", exception)
                // 알 수 없는 오류의 경우 연결을 유지하고 예외만 로깅
            }
        }
    }
}
