package team.mozu.dsm.application.port.out.sse

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface SsePort {

    fun subscribe(clientId: String): SseEmitter

    fun publishTo(clientId: String, eventName: String, data: Any)

    fun publishToAll(eventName: String, data: Any)
}
