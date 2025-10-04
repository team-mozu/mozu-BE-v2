package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.`in`.sse.dto.SSEResponse
import team.mozu.dsm.adapter.out.sse.repository.SseEmitterRepository
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
    private val sseEmitterRepository: SseEmitterRepository
) : LessonOrganSSEUseCase {

    companion object {
        private const val CONNECTED_EVENT = "LESSON_SSE_CONNECTED"
    }

    override fun connect(lessonId: UUID): SseEmitter {
        val lesson = lessonFacade.findByLessonId(lessonId)
        val organ = securityPort.getCurrentOrgan()

        if (lesson.organId != organ.id) {
            throw CannotLessonSSEConnectException
        }

        val clientId = "lesson-organ-sse:${lesson.id}:${organ.id}"
        val emitter = subscribeSsePort.subscribe(clientId)

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
