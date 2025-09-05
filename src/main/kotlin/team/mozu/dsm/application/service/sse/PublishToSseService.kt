package team.mozu.dsm.application.service.sse

import org.springframework.stereotype.Service
import team.mozu.dsm.application.port.`in`.sse.PublishToSseUseCase
import team.mozu.dsm.application.port.out.sse.PublishSsePort

@Service
class PublishToSseService(
    private val publishSsePort: PublishSsePort
) : PublishToSseUseCase {

    override fun publishTo(clientId: String, eventName: String, data: Any) =
        publishSsePort.publishTo(clientId, eventName, data)
}
