package team.mozu.dsm.adapter.out.sse.repository

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Component
class SseEmitterRepository {

    private val emitters = ConcurrentHashMap<String, SseEmitter>()

    fun save(clientId: String, emitter: SseEmitter): SseEmitter {
        emitters[clientId] = emitter
        return emitter
    }

    fun get(clientId: String): SseEmitter? = emitters[clientId]

    fun delete(clientId: String) {
        emitters.remove(clientId)
    }

    fun getAll(): Map<String, SseEmitter> = emitters
}
