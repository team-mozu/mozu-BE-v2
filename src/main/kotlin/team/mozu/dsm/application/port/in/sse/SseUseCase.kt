package team.mozu.dsm.application.port.`in`.sse

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface SseUseCase {

    fun subscribe(clientId: String): SseEmitter

    fun publishTo(clientId: String, eventName: String, data: Any)

    fun publishToAll(eventName: String, data: Any)
}
