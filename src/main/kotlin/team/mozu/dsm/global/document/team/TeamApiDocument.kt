package team.mozu.dsm.global.document.team

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.`in`.team.dto.request.CompleteInvestmentRequest
import team.mozu.dsm.adapter.`in`.team.dto.request.TeamParticipationRequest
import team.mozu.dsm.adapter.`in`.team.dto.response.*
import team.mozu.dsm.global.security.auth.StudentPrincipal
import java.util.*

@Tag(name = "Team", description = "참여 팀 관련 API")
interface TeamApiDocument {

    @Operation(
        summary = "참여 팀 수업 참여",
        description = "새로운 팀에 참여하거나 기존 팀에 합류합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "참여 팀 수업 참여 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = TeamTokenResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 팀 또는 수업",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "409",
            description = "이미 참여한 팀이 있음",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = arrayOf(Content())
        )
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun participate(
        @Valid @RequestBody
        request: TeamParticipationRequest
    ): TeamTokenResponse

    @Operation(
        summary = "참여 팀 투자 완료",
        description = "팀의 투자를 완료하고 결과를 저장합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "투자 완료 처리 성공",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "403",
            description = "권한 없음",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = arrayOf(Content())
        )
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun endInvestment(
        @Valid @RequestBody
        request: List<@Valid CompleteInvestmentRequest>,
        @AuthenticationPrincipal principal: StudentPrincipal
    )

    @Operation(
        summary = "참여 팀 보유 주식 조회",
        description = "참여 팀이 보유한 주식 목록을 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "보유 주식 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = StockResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 팀 또는 수업",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = arrayOf(Content())
        )
    )
    fun getStocks(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): List<StockResponse>

    @Operation(
        summary = "참여 팀 상세 정보 조회",
        description = "현재 로그인한 사용자의 팀 상세 정보를 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "팀 상세 정보 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = TeamDetailResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 팀 또는 수업",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = arrayOf(Content())
        )
    )
    fun getTeamDetail(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): TeamDetailResponse

    @Operation(
        summary = "참여 팀 거래 내역 조회",
        description = "팀의 모든 거래 내역을 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "거래 내역 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = OrderItemResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = arrayOf(Content())
        )
    )
    fun getOrderItems(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): List<OrderItemResponse>

    @Operation(
        summary = "[기관] 참여 팀 거래 현황 조회",
        description = "참여 팀의 현재 거래 현황을 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "거래 현황 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = OrderItemResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 팀",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = arrayOf(Content())
        )
    )
    fun getCurrentOrderItem(
        @PathVariable("team-id") teamId: UUID
    ): List<OrderItemResponse>

    @Operation(
        summary = "참여 팀 결과 요약 조회",
        description = "팀의 투자 결과를 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "결과 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = TeamResultResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 팀 또는 수업",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = arrayOf(Content())
        )
    )
    fun getResult(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): TeamResultResponse

    @Operation(
        summary = "[기관] 학생 보유 주식 조회",
        description = "학생이 보유한 주식 목록을 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "보유 주식 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = StockResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 팀",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = arrayOf(Content())
        )
    )
    fun getHoldStock(
        @PathVariable("team-id") teamId: UUID
    ): List<StockResponse>

    @Operation(
        summary = "참여 팀 수업 랭킹 조회",
        description = "현재 수업의 팀 순위를 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "순위 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = TeamRankResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = arrayOf(Content())
        )
    )
    fun getRank(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): List<TeamRankResponse>

    @Operation(
        summary = "팀 SSE 연결",
        description = "팀 관련 실시간 이벤트를 수신하기 위한 SSE 연결을 설정합니다."
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
            content = arrayOf(Content())
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = arrayOf(Content())
        )
    )
    @GetMapping(
        value = ["/sse"],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
    )
    fun connectTeamSSE(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): SseEmitter
}
