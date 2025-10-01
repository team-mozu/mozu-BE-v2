package team.mozu.dsm.global.document.admin

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.global.error.ErrorResponse
import java.util.UUID

@Tag(name = "Admin", description = "관리자 관련 API")
interface AdminApiDocument {

    @Operation(
        summary = "관리자 SSE 연결",
        description = "관리자가 실시간 이벤트를 수신하기 위한 SSE 연결을 설정합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "SSE 연결 성공",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "403",
            description = "관리자 권한 없음",
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
    @GetMapping(
        value = ["/sse"],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
    )
    fun connectAdminSSE(
        @RequestParam("lessonId") lessonId: UUID,
        authentication: org.springframework.security.core.Authentication
    ): SseEmitter
}
