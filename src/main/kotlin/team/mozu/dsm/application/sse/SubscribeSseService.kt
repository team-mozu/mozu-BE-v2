package team.mozu.dsm.application.sse

import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.application.port.`in`.sse.SubscribeSseUseCase
import team.mozu.dsm.application.port.out.sse.SsePort

@Service
class SubscribeSseService(
    private val ssePort: SsePort
) : SubscribeSseUseCase {

    override fun subscribe(clientId: String): SseEmitter =
        ssePort.subscribe(clientId)
}
