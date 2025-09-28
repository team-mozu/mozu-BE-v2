package team.mozu.dsm.global.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import team.mozu.dsm.application.port.`in`.sse.PublishToAllSseUseCase

@Component
class SseScheduler(
    private val publishToAllSseUseCase: PublishToAllSseUseCase
) {

    @Scheduled(fixedRate = 1000)
    fun sendPing() {
        publishToAllSseUseCase.publishToAll("ping", "ping")
    }
}
