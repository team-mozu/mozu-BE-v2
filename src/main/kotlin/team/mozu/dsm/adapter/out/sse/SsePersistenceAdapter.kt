package team.mozu.dsm.adapter.out.sse

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.out.sse.repository.SseEmitterRepository
import team.mozu.dsm.application.port.out.sse.PublishSsePort
import team.mozu.dsm.application.port.out.sse.SubscribeSsePort
import team.mozu.dsm.global.error.sse.SseExceptionHandler

@Component
class SsePersistenceAdapter(
    private val sseEmitterRepository: SseEmitterRepository,
    private val sseExceptionHandler: SseExceptionHandler
) : SubscribeSsePort, PublishSsePort {

    companion object {
        private const val DEFAULT_TIMEOUT = 60L * 1000 * 60
    }

    //--Subscribe--//
    override fun subscribe(clientId: String): SseEmitter {
        val emitter = SseEmitter(DEFAULT_TIMEOUT)
        sseEmitterRepository.save(clientId, emitter)

        emitter.onCompletion { sseEmitterRepository.delete(clientId) }
        emitter.onTimeout { sseEmitterRepository.delete(clientId) }

        return emitter
    }

    //--Publish--//
    override fun publishTo(clientId: String, eventName: String, data: Any) {
        sseEmitterRepository.get(clientId)?.let { emitter ->
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data))
            } catch (e: Exception) {
                sseExceptionHandler.handle(clientId, emitter, e)
            }
        }
    }

    override fun publishToAll(eventName: String, data: Any) {
        sseEmitterRepository.getAll().forEach { (clientId, emitter) ->
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data))
            } catch (e: Exception) {
                sseExceptionHandler.handle(clientId, emitter, e)
            }
        }
    }
}
