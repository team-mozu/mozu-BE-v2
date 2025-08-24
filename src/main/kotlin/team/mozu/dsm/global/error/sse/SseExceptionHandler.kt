package team.mozu.dsm.global.error.sse

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.out.sse.repository.SseEmitterRepository
import team.mozu.dsm.global.exception.sse.InvalidSseDataException
import team.mozu.dsm.global.exception.sse.InvalidSseStateException
import team.mozu.dsm.global.exception.sse.SseConnectionClosedException
import team.mozu.dsm.global.exception.sse.UnknownSseErrorException
import java.io.IOException

@Component
class SseExceptionHandler(
    private val sseEmitterRepository: SseEmitterRepository
) {

    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    fun handle(clientId: String, emitter: SseEmitter, exception: Exception) {
        when (exception) {
            is IOException -> {
                log.warn("SSE IOException: clientId=$clientId, msg=${exception.message}")
                runCatching { emitter.completeWithError(exception) }
                sseEmitterRepository.delete(clientId)
                throw SseConnectionClosedException
            }
            is IllegalStateException -> {
                log.warn("SSE IllegalState: clientId=$clientId, msg=${exception.message}")
                runCatching { emitter.completeWithError(exception) }
                sseEmitterRepository.delete(clientId)
                throw InvalidSseStateException
            }
            is IllegalArgumentException -> {
                log.error("SSE IllegalArgument: clientId=$clientId, msg=${exception.message}")
                throw InvalidSseDataException
            }
            else -> {
                log.error("Unknown error: clientId=$clientId", exception)
                runCatching { emitter.completeWithError(exception) }
                sseEmitterRepository.delete(clientId)
                throw UnknownSseErrorException
            }
        }
    }
}
