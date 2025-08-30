package team.mozu.dsm.adapter.`in`.lesson

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.mozu.dsm.adapter.`in`.lesson.dto.request.LessonRequest
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.StartLessonResponse
import team.mozu.dsm.application.port.`in`.lesson.CreateLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.StartLessonUseCase
import java.util.UUID

@RestController
@RequestMapping("/lesson")
class LessonWebAdapter(
    private val createLessonUseCase: CreateLessonUseCase,
    private val startLessonUseCase: StartLessonUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody @Valid
        request: LessonRequest
    ): LessonResponse {
        return createLessonUseCase.create(request)
    }

    @PatchMapping("/start/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    fun start(
        @PathVariable("lesson-id") lessonId: UUID
    ): StartLessonResponse {
        return startLessonUseCase.start(lessonId)
    }
}
