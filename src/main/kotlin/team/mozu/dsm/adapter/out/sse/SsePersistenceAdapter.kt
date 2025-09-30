package team.mozu.dsm.adapter.out.sse

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.out.sse.repository.SseEmitterRepository
import team.mozu.dsm.application.port.out.sse.PublishSsePort
import team.mozu.dsm.application.port.out.sse.SubscribeSsePort
import team.mozu.dsm.global.error.sse.SseExceptionHandler

@Component
class SsePersistenceAdapter(
    private val sseEmitterRepository: SseEmitterRepository,
    private val sseExceptionHandler: SseExceptionHandler
) : SubscribeSsePort, PublishSsePort {

    companion object {
        private const val DEFAULT_TIMEOUT = 60L * 1000 * 60 // 1시간
        private val log = org.slf4j.LoggerFactory.getLogger(SsePersistenceAdapter::class.java)
    }

    //--Subscribe--//
    override fun subscribe(clientId: String): SseEmitter {
        log.info("SSE 연결 시작: clientId=$clientId")
        
        // 기존 연결이 있다면 제거
        sseEmitterRepository.get(clientId)?.let { existingEmitter ->
            log.warn("기존 SSE 연결 발견, 제거: clientId=$clientId")
            try {
                existingEmitter.complete()
            } catch (e: Exception) {
                log.debug("기존 연결 종료 중 에러 (무시됨): ${e.message}")
            }
            sseEmitterRepository.delete(clientId)
        }
        
        val emitter = SseEmitter(DEFAULT_TIMEOUT)
        sseEmitterRepository.save(clientId, emitter)

        emitter.onCompletion { 
            log.info("SSE 연결 완료: clientId=$clientId")
            sseEmitterRepository.delete(clientId) 
        }
        emitter.onTimeout { 
            log.warn("SSE 연결 타임아웃: clientId=$clientId")
            sseEmitterRepository.delete(clientId) 
        }
        emitter.onError { throwable ->
            log.error("SSE 연결 에러: clientId=$clientId, error=${throwable?.message}")
            sseEmitterRepository.delete(clientId)
        }

        // 연결 확인 메시지 전송
        try {
            emitter.send(SseEmitter.event().name("connected").data("SSE 연결 성공"))
        } catch (e: Exception) {
            log.error("초기 연결 메시지 전송 실패: clientId=$clientId", e)
            sseEmitterRepository.delete(clientId)
            throw e
        }

        return emitter
    }

    //--Publish--//
    override fun publishTo(clientId: String, eventName: String, data: Any) {
        val emitter = sseEmitterRepository.get(clientId)
        if (emitter == null) {
            log.debug("SSE 클라이언트를 찾을 수 없음: clientId=$clientId, event=$eventName")
            return
        }
        
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data))
            log.debug("SSE 이벤트 전송 성공: clientId=$clientId, event=$eventName")
        } catch (e: Exception) {
            log.error("SSE 이벤트 전송 실패: clientId=$clientId, event=$eventName", e)
            sseExceptionHandler.handle(clientId, emitter, e)
        }
    }

    override fun publishToAll(eventName: String, data: Any) {
        val emitters = sseEmitterRepository.getAll()
        if (emitters.isEmpty()) {
            log.debug("전송할 SSE 클라이언트가 없음: event=$eventName")
            return
        }
        
        var successCount = 0
        var failCount = 0
        
        emitters.forEach { (clientId, emitter) ->
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data))
                successCount++
            } catch (e: Exception) {
                failCount++
                log.debug("SSE 브로드캐스트 실패: clientId=$clientId, event=$eventName, error=${e.message}")
                sseExceptionHandler.handle(clientId, emitter, e)
            }
        }
        
        log.debug("SSE 브로드캐스트 완료: event=$eventName, success=$successCount, fail=$failCount")
    }
}
