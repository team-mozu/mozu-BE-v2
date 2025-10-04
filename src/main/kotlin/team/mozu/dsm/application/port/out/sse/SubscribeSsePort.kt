package team.mozu.dsm.application.port.out.sse

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface SubscribeSsePort {

    fun subscribe(clientId: String): SseEmitter
}
