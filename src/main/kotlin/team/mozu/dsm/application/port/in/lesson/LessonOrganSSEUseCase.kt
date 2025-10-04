package team.mozu.dsm.application.port.`in`.lesson

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID

interface LessonOrganSSEUseCase {

    fun connect(lessonId: UUID): SseEmitter
}
