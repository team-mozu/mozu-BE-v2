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
        summary = "팀 참여",
        description = "새로운 팀에 참여하거나 기존 팀에 합류합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "팀 참여 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = TeamTokenResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 팀 또는 수업"
        ),
        ApiResponse(
            responseCode = "409",
            description = "이미 참여한 팀이 있음"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun participate(
        @Valid @RequestBody
        request: TeamParticipationRequest
    ): TeamTokenResponse

    @Operation(
        summary = "투자 종료",
        description = "팀의 투자 활동을 종료하고 결과를 저장합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "투자 종료 처리 성공"
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
        ),
        ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        ApiResponse(
            responseCode = "403",
            description = "권한 없음"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun endInvestment(
        @Valid @RequestBody
        request: List<@Valid CompleteInvestmentRequest>,
        @AuthenticationPrincipal principal: StudentPrincipal
    )

    @Operation(
        summary = "보유 주식 조회",
        description = "팀이 보유한 주식 목록을 조회합니다."
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
            description = "인증 실패"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 팀 또는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun getStocks(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): List<StockResponse>

    @Operation(
        summary = "팀 상세 정보 조회",
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
            description = "인증 실패"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 팀 또는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun getTeamDetail(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): TeamDetailResponse

    @Operation(
        summary = "주문 내역 조회",
        description = "팀의 모든 주문 내역을 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "주문 내역 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = OrderItemResponse::class))
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
    fun getOrderItems(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): List<OrderItemResponse>

    @Operation(
        summary = "특정 팀의 현재 주문 내역 조회",
        description = "지정된 팀의 현재 주문 내역을 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "주문 내역 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = OrderItemResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 팀"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun getCurrentOrderItem(
        @PathVariable("team-id") teamId: UUID
    ): List<OrderItemResponse>

    @Operation(
        summary = "팀 결과 조회",
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
            description = "인증 실패"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 팀 또는 수업"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun getResult(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): TeamResultResponse

    @Operation(
        summary = "특정 팀의 보유 주식 조회",
        description = "지정된 팀이 보유한 주식 목록을 조회합니다."
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
            description = "존재하지 않는 팀"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun getHoldStock(
        @PathVariable("team-id") teamId: UUID
    ): List<StockResponse>

    @Operation(
        summary = "팀 순위 조회",
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
            description = "인증 실패"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
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
            description = "SSE 연결 성공"
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
    @GetMapping(
        value = ["/sse"],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
    )
    fun connectTeamSSE(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): SseEmitter
}
