package team.mozu.dsm.application.sse

import org.springframework.stereotype.Service
import team.mozu.dsm.application.port.`in`.sse.PublishToSseUseCase
import team.mozu.dsm.application.port.out.sse.SsePort

@Service
class PublishToSseService(
    private val ssePort: SsePort
) : PublishToSseUseCase {

    override fun publishTo(clientId: String, eventName: String, data: Any) =
        ssePort.publishTo(clientId, eventName, data)
}
