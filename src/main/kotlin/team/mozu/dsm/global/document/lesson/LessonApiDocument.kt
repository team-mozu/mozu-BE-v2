package team.mozu.dsm.global.document.lesson

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.`in`.lesson.dto.request.LessonRequest
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonArticleResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemDetailResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonListResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundArticleResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundItemResponse
import team.mozu.dsm.adapter.`in`.lesson.dto.response.StartLessonResponse
import team.mozu.dsm.global.security.auth.StudentPrincipal
import java.util.*

@Tag(name = "Lesson", description = "수업 관련 API")
interface LessonApiDocument {

    @Operation(
        summary = "새로운 수업 생성",
        description = "새로운 수업을 생성합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "수업 생성 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = LessonResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun create(
        @RequestBody @Valid request: LessonRequest
    ): LessonResponse

    @Operation(
        summary = "수업 시작",
        description = "지정된 ID의 수업을 시작합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "수업 시작 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = StartLessonResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun start(
        @PathVariable("lesson-id") lessonId: UUID
    ): StartLessonResponse

    @Operation(
        summary = "수업 즐겨찾기 토글",
        description = "지정된 수업의 즐겨찾기 상태를 변경합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "204",
            description = "즐겨찾기 상태 변경 성공"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun star(
        @PathVariable("lesson-id") lessonId: UUID
    )

    @Operation(
        summary = "수업 삭제",
        description = "지정된 ID의 수업을 삭제합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "204",
            description = "수업 삭제 성공"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun delete(
        @PathVariable("lesson-id") lessonId: UUID
    )

    @Operation(
        summary = "수업 종료",
        description = "지정된 ID의 수업을 종료합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "204",
            description = "수업 종료 성공"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun end(
        @PathVariable("lesson-id") lessonId: UUID
    )

    @Operation(
        summary = "수업 수정",
        description = "지정된 ID의 수업 정보를 수정합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "수업 수정 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = LessonResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun update(
        @PathVariable("lesson-id") lessonId: UUID,
        @RequestBody @Valid request: LessonRequest
    ): LessonResponse

    @Operation(
        summary = "수업 상세 조회",
        description = "지정된 ID의 수업 상세 정보를 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "수업 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = LessonResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun getDetail(
        @PathVariable("lesson-id") lessonId: UUID
    ): LessonResponse

    @Operation(
        summary = "수업 목록 조회",
        description = "등록된 모든 수업의 목록을 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "수업 목록 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = LessonListResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun get(): LessonListResponse

    @Operation(
        summary = "수업의 종목 목록 조회",
        description = "지정된 수업에 속한 모든 종목을 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "종목 목록 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = LessonItemResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun getLessonItems(
        @PathVariable("lesson-id") lessonId: UUID
    ): List<LessonItemResponse>

    @Operation(
        summary = "수업의 기사 목록 조회",
        description = "지정된 수업에 속한 모든 기사를 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "기사 목록 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = LessonArticleResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun getLessonArticles(
        @PathVariable("lesson-id") lessonId: UUID
    ): List<LessonArticleResponse>

    @Operation(
        summary = "다음 단계로 진행",
        description = "수업의 다음 단계로 진행합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "204",
            description = "다음 단계로 진행 성공"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun next(
        @PathVariable("lesson-id") lessonId: UUID
    )

    @Operation(
        summary = "SSE 연결",
        description = "수업 진행 상황을 실시간으로 받기 위한 SSE 연결을 설정합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "SSE 연결 성공"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun sse(
        @PathVariable("lesson-id") lessonId: UUID
    ): SseEmitter

    @Operation(
        summary = "팀별 종목 라운드 조회",
        description = "현재 사용자의 팀에 해당하는 종목 라운드 정보를 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "팀별 종목 라운드 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = LessonRoundItemResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun getLessonRoundItems(
        @AuthenticationPrincipal studentPrincipal: StudentPrincipal
    ): List<LessonRoundItemResponse>

    @Operation(
        summary = "팀별 기사 라운드 조회",
        description = "현재 사용자의 팀에 해당하는 기사 라운드 정보를 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "팀별 기사 라운드 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = LessonRoundArticleResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun getLessonRoundArticles(
        @AuthenticationPrincipal studentPrincipal: StudentPrincipal
    ): List<LessonRoundArticleResponse>

    @Operation(
        summary = "팀별 종목 상세 조회",
        description = "현재 사용자의 팀에 해당하는 특정 종목의 상세 정보를 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "팀별 종목 상세 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = LessonItemDetailResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 종목"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun getLessonItemDetail(
        @AuthenticationPrincipal studentPrincipal: StudentPrincipal,
        @PathVariable("item-id") itemId: UUID
    ): LessonItemDetailResponse
}
