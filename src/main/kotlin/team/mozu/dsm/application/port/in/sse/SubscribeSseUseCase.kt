package team.mozu.dsm.application.port.`in`.sse

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface SubscribeSseUseCase {

    fun execute(clientId: String): SseEmitter
}
