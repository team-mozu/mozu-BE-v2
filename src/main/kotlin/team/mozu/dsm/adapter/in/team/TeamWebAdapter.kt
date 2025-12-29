package team.mozu.dsm.adapter.`in`.team

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.`in`.team.dto.request.CompleteInvestmentRequest
import team.mozu.dsm.adapter.`in`.team.dto.request.TeamParticipationRequest
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamRankResponse
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamResultResponse
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamTokenResponse
import team.mozu.dsm.adapter.`in`.team.dto.response.StockResponse
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamDetailResponse
import team.mozu.dsm.adapter.`in`.team.dto.response.OrderItemResponse
import team.mozu.dsm.adapter.`in`.team.dto.response.TradingDetailResponse
import team.mozu.dsm.application.port.`in`.team.TeamParticipationUseCase
import team.mozu.dsm.application.port.`in`.team.CompleteTeamInvestmentUseCase
import team.mozu.dsm.application.port.`in`.team.QueryHoldStocksUseCase
import team.mozu.dsm.application.port.`in`.team.ConnectTeamSSEUseCase
import team.mozu.dsm.application.port.`in`.team.QueryCurrentOrderItemsUseCase
import team.mozu.dsm.application.port.`in`.team.QueryStocksUseCase
import team.mozu.dsm.application.port.`in`.team.QueryTeamDetailUseCase
import team.mozu.dsm.application.port.`in`.team.QueryOrderItemsUseCase
import team.mozu.dsm.application.port.`in`.team.QueryTeamResultUseCase
import team.mozu.dsm.application.port.`in`.team.QueryTeamRanksUseCase
import team.mozu.dsm.application.port.`in`.team.QueryTradingDetailUseCase
import team.mozu.dsm.global.document.team.TeamApiDocument
import team.mozu.dsm.global.security.auth.StudentPrincipal
import java.util.UUID

@RestController
@RequestMapping("/team")
class TeamWebAdapter(
    private val teamParticipationUseCase: TeamParticipationUseCase,
    private val teamInvestmentUseCase: CompleteTeamInvestmentUseCase,
    private val queryStocksUseCase: QueryStocksUseCase,
    private val queryTeamDetailUseCase: QueryTeamDetailUseCase,
    private val queryOrderItemsUseCase: QueryOrderItemsUseCase,
    private val queryCurrentOrderItemsUseCase: QueryCurrentOrderItemsUseCase,
    private val queryTeamResultUseCase: QueryTeamResultUseCase,
    private val queryHoldStocksUseCase: QueryHoldStocksUseCase,
    private val queryTeamRanksUseCase: QueryTeamRanksUseCase,
    private val queryTradingDetailUseCase: QueryTradingDetailUseCase,
    private val connectTeamSSEUseCase: ConnectTeamSSEUseCase
) : TeamApiDocument {

    @PostMapping("/participate")
    @ResponseStatus(HttpStatus.CREATED)
    override fun participate(
        @Valid @RequestBody
        request: TeamParticipationRequest
    ): TeamTokenResponse =
        teamParticipationUseCase.execute(request)

    @PostMapping("/end")
    @ResponseStatus(HttpStatus.CREATED)
    override fun endInvestment(
        @Valid @RequestBody
        request: List<@Valid CompleteInvestmentRequest>,
        @AuthenticationPrincipal principal: StudentPrincipal
    ) = teamInvestmentUseCase.execute(request, principal.lessonNum, principal.teamId)

    @GetMapping("/stocks")
    @ResponseStatus(HttpStatus.OK)
    override fun queryAllStock(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): List<StockResponse> =
        queryStocksUseCase.execute(principal.lessonNum, principal.teamId)

    @GetMapping("/detail")
    @ResponseStatus(HttpStatus.OK)
    override fun queryDetail(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): TeamDetailResponse =
        queryTeamDetailUseCase.execute(principal.lessonNum, principal.teamId)

    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    override fun queryAllOrderItem(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): List<OrderItemResponse> =
        queryOrderItemsUseCase.execute(principal.teamId)

    @GetMapping("/{team-id}")
    @ResponseStatus(HttpStatus.OK)
    override fun queryAllCurrentOrderItem(
        @PathVariable("team-id") teamId: UUID
    ): List<OrderItemResponse> =
        queryCurrentOrderItemsUseCase.execute(teamId)

    @GetMapping("/result")
    @ResponseStatus(HttpStatus.OK)
    override fun queryResult(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): TeamResultResponse =
        queryTeamResultUseCase.execute(principal.lessonNum, principal.teamId)

    @GetMapping("/{team-id}/holdItems")
    @ResponseStatus(HttpStatus.OK)
    override fun queryAllHoldStock(
        @PathVariable("team-id") teamId: UUID
    ): List<StockResponse> =
        queryHoldStocksUseCase.execute(teamId)

    @GetMapping("/ranks")
    @ResponseStatus(HttpStatus.OK)
    override fun queryRank(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): List<TeamRankResponse> =
        queryTeamRanksUseCase.execute(principal.lessonNum, principal.teamId)

    @GetMapping("/trading-detail")
    @ResponseStatus(HttpStatus.OK)
    fun queryTradingDetail(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): List<TradingDetailResponse> =
        queryTradingDetailUseCase.execute(principal.lessonNum, principal.teamId)

    @GetMapping("/sse")
    @ResponseStatus(HttpStatus.OK)
    override fun connectTeamSSE(
        @AuthenticationPrincipal principal: StudentPrincipal,
        @RequestHeader(value = "Last-Event-ID", required = false) lastEventId: String?
    ): SseEmitter =
        connectTeamSSEUseCase.execute(principal.teamId, lastEventId)
}
