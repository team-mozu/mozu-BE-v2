package team.mozu.dsm.adapter.out.sse

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.out.sse.repository.SseEmitterRepository
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
        val event = SseEvent.create(clientId, eventName, data.toString())
        sseEventStorePort.save(event)

        sseEmitterRepository.get(clientId)?.let { emitter ->
            try {
                emitter.send(
                    SseEmitter.event()
                        .id(event.id)
                        .name(eventName)
                        .data(data)
                )
            } catch (e: Exception) {
                sseExceptionHandler.handle(clientId, emitter, e)
            }
        }
    }

    override fun publishToAll(eventName: String, data: Any) {
        sseEmitterRepository.getAll().forEach { (clientId, emitter) ->
            val event = SseEvent.create(clientId, eventName, data.toString())
            sseEventStorePort.save(event)

            try {
                emitter.send(
                    SseEmitter.event()
                        .id(event.id)
                        .name(eventName)
                        .data(data)
                )
            } catch (e: Exception) {
                sseExceptionHandler.handle(clientId, emitter, e)
            }
        }
    }

    fun publishToTeam(teamId: UUID, eventName: String, data: Any) {
        sseEmitterRepository.getAll().forEach { (clientId, emitter) ->
            val event = SseEvent.create(clientId, eventName, data.toString(), teamId)
            sseEventStorePort.save(event)

            try {
                emitter.send(
                    SseEmitter.event()
                        .id(event.id)
                        .name(eventName)
                        .data(data)
                )
            } catch (e: Exception) {
                sseExceptionHandler.handle(clientId, emitter, e)
            }
        }
    }

    fun subscribeWithRecovery(clientId: String, lastEventId: String?, teamId: UUID? = null): SseEmitter {
        val emitter = SseEmitter(DEFAULT_TIMEOUT)
        sseEmitterRepository.save(clientId, emitter)

        emitter.onCompletion { sseEmitterRepository.delete(clientId) }
        emitter.onTimeout { sseEmitterRepository.delete(clientId) }

        // Send missed events if lastEventId is provided
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
