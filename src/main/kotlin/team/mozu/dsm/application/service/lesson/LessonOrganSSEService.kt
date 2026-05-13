package team.mozu.dsm.application.service.lesson

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.`in`.sse.dto.SSEResponse
import team.mozu.dsm.adapter.out.sse.persistence.SsePersistenceAdapter
import team.mozu.dsm.adapter.out.sse.persistence.repository.SseEmitterRepository
import team.mozu.dsm.application.exception.lesson.CannotLessonSSEConnectException
import team.mozu.dsm.application.port.`in`.lesson.LessonOrganSSEUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.sse.SubscribeSsePort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class LessonOrganSSEService(
    private val subscribeSsePort: SubscribeSsePort,
    private val lessonFacade: LessonFacade,
    private val securityPort: SecurityPort,
    private val sseEmitterRepository: SseEmitterRepository,
    private val ssePersistenceAdapter: SsePersistenceAdapter
) : LessonOrganSSEUseCase {

    companion object {
        private const val CONNECTED_EVENT = "LESSON_SSE_CONNECTED"
        private val logger = LoggerFactory.getLogger(LessonOrganSSEService::class.java)
    }

    override fun execute(lessonId: UUID, lastEventId: String?): SseEmitter {
        logger.info("Admin SSE 연결 시도 - LessonId: $lessonId, LastEventId: $lastEventId")
        val lesson = lessonFacade.findByLessonId(lessonId)
        val organ = securityPort.getCurrentOrgan()

        if (lesson.organId != organ.id) {
            throw CannotLessonSSEConnectException
        }

        val clientId = "lesson-organ-sse:${lesson.id}:${organ.id}"
        val emitter = if (lastEventId != null) {
            logger.info("Admin SSE 이벤트 복구 모드 - ClientId: $clientId, LastEventId: $lastEventId")
            ssePersistenceAdapter.subscribeWithRecovery(clientId, lastEventId)
        } else {
            logger.info("Admin SSE 새 연결 - ClientId: $clientId")
            subscribeSsePort.subscribe(clientId)
        }

        try {
            emitter.send(
                SseEmitter.event()
                    .name(CONNECTED_EVENT)
                    .data(SSEResponse(CONNECTED_EVENT, "id ${lesson.id}의 수업 기관 클라이언트 SSE 연결되었습니다."))
            )
        } catch (e: Exception) {
            sseEmitterRepository.delete(clientId)
            emitter.completeWithError(e)
            throw e
        }

        return emitter
    }
}
