package team.mozu.dsm.adapter.`in`.lesson

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import team.mozu.dsm.adapter.`in`.lesson.dto.request.LessonRequest
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonListResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.StartLessonResponse
import team.mozu.dsm.application.port.`in`.lesson.ChangeStarredUseCase
import team.mozu.dsm.application.port.`in`.lesson.CreateLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.DeleteLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.StartLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.EndLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.UpdateLessonUseCase
import java.util.UUID

@RestController
@RequestMapping("/lesson")
class LessonWebAdapter(
    private val createLessonUseCase: CreateLessonUseCase,
    private val startLessonUseCase: StartLessonUseCase,
    private val changeStarredUseCase: ChangeStarredUseCase,
    private val deleteLessonUseCase: DeleteLessonUseCase,
    private val endLessonUseCase: EndLessonUseCase,
    private val updateLessonUseCase: UpdateLessonUseCase,
    private val getLessonDetailUseCase: GetLessonDetailUseCase,
    private val getLessonsUseCase: GetLessonsUseCase
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

    @PatchMapping("/star/{lesson-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun star(
        @PathVariable("lesson-id") lessonId: UUID
    ) {
        changeStarredUseCase.change(lessonId)
    }

    @DeleteMapping("/{lesson-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable("lesson-id") lessonId: UUID
    ) {
        deleteLessonUseCase.delete(lessonId)
    }

    @PatchMapping("/end/{lesson-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun end(
        @PathVariable("lesson-id") lessonId: UUID
    ) {
        endLessonUseCase.end(lessonId)
    }

    @PatchMapping("/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(
        @PathVariable("lesson-id") lessonId: UUID,
        @RequestBody @Valid
        request: LessonRequest
    ): LessonResponse {
        return updateLessonUseCase.update(lessonId, request)
    }

    @GetMapping("/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    fun getDetail(
        @PathVariable("lesson-id") lessonId: UUID
    ): LessonResponse {
        return getLessonDetailUseCase.get(lessonId)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun get(): LessonListResponse {
        return getLessonsUseCase.get()
    }
}
