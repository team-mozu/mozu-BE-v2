package team.mozu.dsm.global.document.article

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import team.mozu.dsm.adapter.`in`.article.dto.request.ArticleRequest
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.global.error.ErrorResponse
import java.util.*

@Tag(name = "Article", description = "기사 관련 API")
interface ArticleApiDocument {

    @Operation(
        summary = "기사 생성",
        description = "새로운 기사를 생성합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "기사 생성 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ArticleResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )
            ]
        )
    )
    fun create(
        @RequestBody @Valid
        request: ArticleRequest
    ): ArticleResponse

    @Operation(
        summary = "기사 상세 조회",
        description = "지정된 ID의 기사 상세 정보를 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "기사 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ArticleResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 기사",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )
            ]
        )
    )
    fun queryDetail(
        @PathVariable id: UUID
    ): ArticleResponse

    @Operation(
        summary = "기사 목록 조회",
        description = "등록된 모든 기사의 목록을 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "기사 목록 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = ArticleResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )
            ]
        )
    )
    fun queryAll(): List<ArticleResponse>

    @Operation(
        summary = "기사 삭제",
        description = "지정된 ID의 기사를 삭제합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "204",
            description = "기사 삭제 성공",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 기사",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )
            ]
        )
    )
    fun delete(
        @PathVariable id: UUID
    )

    @Operation(
        summary = "기사 수정",
        description = "지정된 ID의 기사 정보를 수정합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "기사 수정 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ArticleResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 기사",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )
            ]
        )
    )
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid
        request: ArticleRequest
    ): ArticleResponse
}
