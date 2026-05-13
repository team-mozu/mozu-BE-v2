package team.mozu.dsm.adapter.out.sse.persistence

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.out.sse.persistence.repository.SseEmitterRepository
import team.mozu.dsm.application.port.out.sse.PublishSsePort
import team.mozu.dsm.application.port.out.sse.SubscribeSsePort
import team.mozu.dsm.application.port.out.sse.SseEventStorePort
import team.mozu.dsm.domain.sse.model.SseEvent
import team.mozu.dsm.global.error.sse.SseExceptionHandler
import java.util.UUID

@Component
class SsePersistenceAdapter(
    private val sseEmitterRepository: SseEmitterRepository,
    private val sseExceptionHandler: SseExceptionHandler,
    private val sseEventStorePort: SseEventStorePort
) : SubscribeSsePort, PublishSsePort {

    companion object {
        private const val DEFAULT_TIMEOUT = 60L * 1000 * 60
        private val logger = LoggerFactory.getLogger(SsePersistenceAdapter::class.java)
    }

    override fun subscribe(clientId: String): SseEmitter {
        val emitter = SseEmitter(DEFAULT_TIMEOUT)
        sseEmitterRepository.save(clientId, emitter)

        emitter.onCompletion { sseEmitterRepository.delete(clientId) }
        emitter.onTimeout { sseEmitterRepository.delete(clientId) }

        return emitter
    }

    override fun publishTo(clientId: String, eventName: String, data: Any) {
        val event = SseEvent.create(clientId, eventName, data.toString())
        sseEventStorePort.save(event)

        val emitter = sseEmitterRepository.get(clientId)
        if (emitter == null) {
            logger.warn(
                "SSE publish: emitter not in pool — clientId={}, eventName={}, eventId={}. " +
                    "Stored in event store; recovery requires reconnect with Last-Event-ID.",
                clientId, eventName, event.id
            )
            return
        }

        try {
            emitter.send(
                SseEmitter.event()
                    .id(event.id)
                    .name(eventName)
                    .data(data)
            )
        } catch (e: Exception) {
            logger.warn("SSE publish failed — clientId={}, eventName={}, eventId={}: {}", clientId, eventName, event.id, e.message)
            sseExceptionHandler.handle(clientId, emitter, e)
        }
    }

    fun subscribeWithRecovery(clientId: String, lastEventId: String?, teamId: UUID? = null): SseEmitter {
        val emitter = SseEmitter(DEFAULT_TIMEOUT)
        sseEmitterRepository.save(clientId, emitter)

        emitter.onCompletion { sseEmitterRepository.delete(clientId) }
        emitter.onTimeout { sseEmitterRepository.delete(clientId) }

        try {
            val missedEvents = if (teamId != null) {
                sseEventStorePort.findTeamEventsAfter(teamId, lastEventId)
            } else {
                sseEventStorePort.findEventsAfter(clientId, lastEventId)
            }

            missedEvents.forEach { event ->
                emitter.send(
                    SseEmitter.event()
                        .id(event.id)
                        .name(event.eventName)
                        .data(event.data)
                        .comment("recovered")
                )
            }
        } catch (e: Exception) {
            sseExceptionHandler.handle(clientId, emitter, e)
        }

        return emitter
    }
}
