package team.mozu.dsm.global.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.out.sse.persistence.repository.SseEmitterRepository

@Component
class SseScheduler(
    private val sseEmitterRepository: SseEmitterRepository
) {

    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedRate = 30_000)
    fun sendPing() {
        val emitters = sseEmitterRepository.getAll().toMap()
        emitters.forEach { (clientId, emitter) ->
            try {
                emitter.send(SseEmitter.event().comment("ping"))
            } catch (e: Exception) {
                log.debug("Removing dead SSE connection on ping: clientId=$clientId")
                try {
                    emitter.completeWithError(e)
                } catch (_: Exception) {
                    // emitter already completed
                }
                sseEmitterRepository.delete(clientId)
            }
        }
    }

    @Scheduled(fixedRate = 300_000)
    fun cleanupDeadConnections() {
        val emitters = sseEmitterRepository.getAll().toMap()
        var cleanedCount = 0

        emitters.forEach { (clientId, emitter) ->
            try {
                emitter.send(SseEmitter.event().comment(""))
            } catch (e: Exception) {
                log.debug("Removing dead SSE connection: clientId=$clientId")
                try {
                    emitter.completeWithError(e)
                } catch (_: Exception) {
                    // emitter already completed
                }
                sseEmitterRepository.delete(clientId)
                cleanedCount++
            }
        }

        if (cleanedCount > 0) {
            log.info("Cleaned up $cleanedCount dead SSE connections")
        }
    }
}
