package team.mozu.dsm.application.service.sse

import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.application.port.`in`.sse.SubscribeSseUseCase
import team.mozu.dsm.application.port.out.sse.SubscribeSsePort

@Service
class SubscribeSseService(
    private val subscribeSsePort: SubscribeSsePort
) : SubscribeSseUseCase {

    override fun subscribe(clientId: String): SseEmitter =
        subscribeSsePort.subscribe(clientId)
}
