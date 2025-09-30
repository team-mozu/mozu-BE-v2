package team.mozu.dsm.global.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.out.sse.repository.SseEmitterRepository
import team.mozu.dsm.application.port.`in`.sse.PublishToAllSseUseCase

@Component
class SseScheduler(
    private val publishToAllSseUseCase: PublishToAllSseUseCase,
    private val sseEmitterRepository: SseEmitterRepository
) {

    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedRate = 1000)
    fun sendPing() {
        publishToAllSseUseCase.publishToAll("ping", "ping")
    }

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    fun cleanupDeadConnections() {
        val emitters = sseEmitterRepository.getAll().toMap()
        var cleanedCount = 0

        emitters.forEach { (clientId, emitter) ->
            try {
                // 빈 이벤트로 연결 상태 확인
                emitter.send(SseEmitter.event().comment(""))
            } catch (e: Exception) {
                log.debug("Removing dead SSE connection: clientId=$clientId")
                sseEmitterRepository.delete(clientId)
                cleanedCount++
            }
        }

        if (cleanedCount > 0) {
            log.info("Cleaned up $cleanedCount dead SSE connections")
        }
    }
}
