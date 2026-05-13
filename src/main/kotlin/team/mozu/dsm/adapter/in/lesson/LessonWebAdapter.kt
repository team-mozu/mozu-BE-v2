package team.mozu.dsm.adapter.`in`.lesson

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
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
import team.mozu.dsm.adapter.`in`.lesson.dto.response.ParticipatingTeamResponse
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
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonDetailUseCase
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonsUseCase
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonItemsUseCase
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonArticlesUseCase
import team.mozu.dsm.application.port.`in`.lesson.NextLessonUseCase
import team.mozu.dsm.application.port.`in`.lesson.StartLessonInvestmentUseCase
import team.mozu.dsm.application.port.`in`.lesson.LessonOrganSSEUseCase
import team.mozu.dsm.application.port.`in`.lesson.QueryParticipatingTeamsUseCase
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonRoundItemsUseCase
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonRoundArticlesUseCase
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonItemDetailUseCase
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
    private val queryLessonDetailUseCase: QueryLessonDetailUseCase,
    private val queryLessonsUseCase: QueryLessonsUseCase,
    private val queryLessonItemsUseCase: QueryLessonItemsUseCase,
    private val queryLessonArticlesUseCase: QueryLessonArticlesUseCase,
    private val nextLessonUseCase: NextLessonUseCase,
    private val startLessonInvestmentUseCase: StartLessonInvestmentUseCase,
    private val lessonOrganSSEUseCase: LessonOrganSSEUseCase,
    private val queryParticipatingTeamsUseCase: QueryParticipatingTeamsUseCase,
    private val queryLessonRoundItemsUseCase: QueryLessonRoundItemsUseCase,
    private val queryLessonRoundArticlesUseCase: QueryLessonRoundArticlesUseCase,
    private val queryLessonItemDetailUseCase: QueryLessonItemDetailUseCase
) : LessonApiDocument {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun create(
        @RequestBody @Valid
        request: LessonRequest
    ): LessonResponse = createLessonUseCase.execute(request)

    @PatchMapping("/start/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun start(
        @PathVariable("lesson-id") lessonId: UUID
    ): StartLessonResponse = startLessonUseCase.execute(lessonId)

    @PatchMapping("/star/{lesson-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun star(
        @PathVariable("lesson-id") lessonId: UUID
    ) = changeStarUseCase.execute(lessonId)

    @DeleteMapping("/{lesson-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun delete(
        @PathVariable("lesson-id") lessonId: UUID
    ) = deleteLessonUseCase.execute(lessonId)

    @PatchMapping("/end/{lesson-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun end(
        @PathVariable("lesson-id") lessonId: UUID
    ) = endLessonUseCase.execute(lessonId)

    @PatchMapping("/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun update(
        @PathVariable("lesson-id") lessonId: UUID,
        @RequestBody @Valid
        request: LessonRequest
    ): LessonResponse = updateLessonUseCase.execute(lessonId, request)

    @GetMapping("/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun queryDetail(
        @PathVariable("lesson-id") lessonId: UUID
    ): LessonResponse = queryLessonDetailUseCase.execute(lessonId)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun queryAll(): LessonListResponse =
        queryLessonsUseCase.execute()

    @GetMapping("/items/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun queryAllLessonItem(
        @PathVariable("lesson-id") lessonId: UUID
    ): List<LessonItemResponse> =
        queryLessonItemsUseCase.execute(lessonId)

    @GetMapping("/articles/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun queryAllLessonArticle(
        @PathVariable("lesson-id") lessonId: UUID
    ): List<LessonArticleResponse> =
        queryLessonArticlesUseCase.execute(lessonId)

    @PatchMapping("/next/{lesson-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun next(
        @PathVariable("lesson-id") lessonId: UUID
    ) = nextLessonUseCase.execute(lessonId)

    @PatchMapping("/start-investment/{lesson-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun startInvestment(
        @PathVariable("lesson-id") lessonId: UUID
    ) = startLessonInvestmentUseCase.execute(lessonId)

    @GetMapping("/sse/{lesson-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun sse(
        @PathVariable("lesson-id") lessonId: UUID,
        @RequestHeader(value = "Last-Event-ID", required = false) lastEventId: String?
    ): SseEmitter = lessonOrganSSEUseCase.execute(lessonId, lastEventId)

    @GetMapping("/{lesson-id}/teams")
    @ResponseStatus(HttpStatus.OK)
    fun queryParticipatingTeams(
        @PathVariable("lesson-id") lessonId: UUID
    ): List<ParticipatingTeamResponse> = queryParticipatingTeamsUseCase.execute(lessonId)

    @GetMapping("/team/items")
    @ResponseStatus(HttpStatus.OK)
    override fun queryAllLessonRoundItem(
        @AuthenticationPrincipal studentPrincipal: StudentPrincipal
    ): List<LessonRoundItemResponse> =
        queryLessonRoundItemsUseCase.execute(studentPrincipal.lessonNum)

    @GetMapping("/team/articles")
    @ResponseStatus(HttpStatus.OK)
    override fun queryAllLessonRoundArticle(
        @AuthenticationPrincipal studentPrincipal: StudentPrincipal
    ): List<LessonRoundArticleResponse> =
        queryLessonRoundArticlesUseCase.execute(studentPrincipal.lessonNum)

    @GetMapping("/team/item/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun queryLessonItemDetail(
        @AuthenticationPrincipal studentPrincipal: StudentPrincipal,
        @PathVariable("item-id") itemId: Int
    ): LessonItemDetailResponse =
        queryLessonItemDetailUseCase.execute(studentPrincipal.lessonNum, itemId)
}
