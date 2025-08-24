package team.mozu.dsm.application.sse

import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.application.port.`in`.sse.SseUseCase
import team.mozu.dsm.application.port.out.sse.SsePort

@Service
class SseService(
    private val ssePort: SsePort
) : SseUseCase {

    override fun subscribe(clientId: String): SseEmitter =
        ssePort.subscribe(clientId)

    override fun publishTo(clientId: String, eventName: String, data: Any) =
        ssePort.publishTo(clientId, eventName, data)

    override fun publishToAll(eventName: String, data: Any) =
        ssePort.publishToAll(eventName, data)
}
