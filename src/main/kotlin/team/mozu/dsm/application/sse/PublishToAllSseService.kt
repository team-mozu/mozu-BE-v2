package team.mozu.dsm.application.sse

import org.springframework.stereotype.Service
import team.mozu.dsm.application.port.`in`.sse.PublishToAllSseUseCase
import team.mozu.dsm.application.port.out.sse.SsePort

@Service
class PublishToAllSseService(
    private val ssePort: SsePort
) : PublishToAllSseUseCase {

    override fun publishToAll(eventName: String, data: Any) =
        ssePort.publishToAll(eventName, data)
}
