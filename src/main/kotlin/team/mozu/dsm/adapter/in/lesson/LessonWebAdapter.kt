package team.mozu.dsm.adapter.`in`.lesson

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.mozu.dsm.adapter.`in`.lesson.dto.request.LessonRequest
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonResponse
import team.mozu.dsm.application.port.`in`.lesson.CreateLessonUseCase

@RestController
@RequestMapping("/lesson")
class LessonWebAdapter(
    private val createLessonUseCase: CreateLessonUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody @Valid
        request: LessonRequest
    ): LessonResponse {
        return createLessonUseCase.create(request)
    }
}
