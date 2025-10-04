package team.mozu.dsm.application.service.sse

import org.springframework.stereotype.Service
import team.mozu.dsm.application.port.`in`.sse.PublishToAllSseUseCase
import team.mozu.dsm.application.port.out.sse.PublishSsePort

@Service
class PublishToAllSseService(
    private val publishSsePort: PublishSsePort
) : PublishToAllSseUseCase {

    override fun publishToAll(eventName: String, data: Any) =
        publishSsePort.publishToAll(eventName, data)
}
