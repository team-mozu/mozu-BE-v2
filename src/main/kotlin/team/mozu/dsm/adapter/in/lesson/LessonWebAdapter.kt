package team.mozu.dsm.adapter.`in`.lesson

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.`in`.lesson.dto.request.LessonRequest
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonListResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.StartLessonResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonArticleResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundItemResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundArticleResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemDetailResponse
import team.mozu.dsm.application.port.`in`.lesson.ChangeStarUseCase
import team.mozu.dsm.application.port.`in`.lesson.CreateLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.DeleteLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.StartLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.EndLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.UpdateLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.GetLessonDetailUseCase
import team.mozu.dsm.application.port.`in`.lesson.GetLessonsUseCase
import team.mozu.dsm.application.port.`in`.lesson.GetLessonItemsUseCase
import team.mozu.dsm.application.port.`in`.lesson.GetLessonArticlesUseCase
import team.mozu.dsm.application.port.`in`.lesson.NextLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.LessonOrganSSEUseCase
import team.mozu.dsm.application.port.`in`.lesson.GetLessonRoundItemsUseCase
import team.mozu.dsm.application.port.`in`.lesson.GetLessonRoundArticlesUseCase
import team.mozu.dsm.application.port.`in`.lesson.GetLessonItemDetailUseCase
import team.mozu.dsm.global.document.lesson.LessonApiDocument
import team.mozu.dsm.global.security.auth.StudentPrincipal
import java.util.UUID

@RestController
@RequestMapping("/lesson")
class LessonWebAdapter(
    private val createLessonUseCase: CreateLessonUseCase,
    private val startLessonUseCase: StartLessonUseCase,
    private val changeStarUseCase: ChangeStarUseCase,
    private val deleteLessonUseCase: DeleteLessonUseCase,
    private val endLessonUseCase: EndLessonUseCase,
    private val updateLessonUseCase: UpdateLessonUseCase,
    private val getLessonDetailUseCase: GetLessonDetailUseCase,
    private val getLessonsUseCase: GetLessonsUseCase,
    private val getLessonItemsUseCase: GetLessonItemsUseCase,
    private val getLessonArticlesUseCase: GetLessonArticlesUseCase,
    private val nextLessonUseCase: NextLessonUseCase,
    private val lessonOrganSSEUseCase: LessonOrganSSEUseCase,
    private val getLessonRoundItemsUseCase: GetLessonRoundItemsUseCase,
    private val getLessonRoundArticlesUseCase: GetLessonRoundArticlesUseCase,
    private val getLessonItemDetailUseCase: GetLessonItemDetailUseCase
) : LessonApiDocument {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun create(
        @RequestBody @Valid
        request: LessonRequest
    ): LessonResponse {
        return createLessonUseCase.create(request)
    }

    @PatchMapping("/start/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun start(
        @PathVariable("lesson-id") lessonId: UUID
    ): StartLessonResponse {
        return startLessonUseCase.start(lessonId)
    }

    @PatchMapping("/star/{lesson-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun star(
        @PathVariable("lesson-id") lessonId: UUID
    ) {
        changeStarUseCase.change(lessonId)
    }

    @DeleteMapping("/{lesson-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun delete(
        @PathVariable("lesson-id") lessonId: UUID
    ) {
        deleteLessonUseCase.delete(lessonId)
    }

    @PatchMapping("/end/{lesson-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun end(
        @PathVariable("lesson-id") lessonId: UUID
    ) {
        endLessonUseCase.end(lessonId)
    }

    @PatchMapping("/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun update(
        @PathVariable("lesson-id") lessonId: UUID,
        @RequestBody @Valid
        request: LessonRequest
    ): LessonResponse {
        return updateLessonUseCase.update(lessonId, request)
    }

    @GetMapping("/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun getDetail(
        @PathVariable("lesson-id") lessonId: UUID
    ): LessonResponse {
        return getLessonDetailUseCase.get(lessonId)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun get(): LessonListResponse {
        return getLessonsUseCase.get()
    }

    @GetMapping("/items/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun getLessonItems(
        @PathVariable("lesson-id") lessonId: UUID
    ): List<LessonItemResponse> {
        return getLessonItemsUseCase.get(lessonId)
    }

    @GetMapping("/articles/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun getLessonArticles(
        @PathVariable("lesson-id") lessonId: UUID
    ): List<LessonArticleResponse> {
        return getLessonArticlesUseCase.get(lessonId)
    }

    @PatchMapping("/next/{lesson-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun next(
        @PathVariable("lesson-id") lessonId: UUID
    ) {
        nextLessonUseCase.next(lessonId)
    }

    @GetMapping("/sse/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun sse(
        @PathVariable("lesson-id") lessonId: UUID
    ): SseEmitter {
        return lessonOrganSSEUseCase.connect(lessonId)
    }

    @GetMapping("/team/items")
    @ResponseStatus(HttpStatus.OK)
    override fun getLessonRoundItems(
        @AuthenticationPrincipal studentPrincipal: StudentPrincipal
    ): List<LessonRoundItemResponse> {
        return getLessonRoundItemsUseCase.get(studentPrincipal.lessonNum)
    }

    @GetMapping("/team/articles")
    @ResponseStatus(HttpStatus.OK)
    override fun getLessonRoundArticles(
        @AuthenticationPrincipal studentPrincipal: StudentPrincipal
    ): List<LessonRoundArticleResponse> {
        return getLessonRoundArticlesUseCase.get(studentPrincipal.lessonNum)
    }

    @GetMapping("/team/item/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun getLessonItemDetail(
        @AuthenticationPrincipal studentPrincipal: StudentPrincipal,
        @PathVariable("item-id") lessonId: UUID
    ): LessonItemDetailResponse {
        return getLessonItemDetailUseCase.get(studentPrincipal.lessonNum, lessonId)
    }
}
