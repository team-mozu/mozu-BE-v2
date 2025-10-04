package team.mozu.dsm.global.document.sse

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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

@Tag(name = "SSE", description = "Server-Sent Events 관련 API")
interface SseApiDocument {

    @Operation(
        summary = "SSE 구독",
        description = "클라이언트 ID를 사용하여 Server-Sent Events(SSE)를 구독합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "SSE 연결 성공",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 클라이언트 ID",
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
        value = ["/subscribe"],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
    )
    fun subscribe(
        @Parameter(description = "클라이언트 고유 식별자", required = true, example = "client-1234")
        @RequestParam
        clientId: String
    ): SseEmitter
}
