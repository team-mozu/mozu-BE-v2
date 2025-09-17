package team.mozu.dsm.global.document.organ

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import team.mozu.dsm.adapter.`in`.auth.dto.response.TokenResponse
import team.mozu.dsm.adapter.`in`.organ.dto.request.CreateOrganRequest
import team.mozu.dsm.adapter.`in`.organ.dto.request.LoginOrganRequest
import team.mozu.dsm.adapter.`in`.organ.dto.request.ReissueOrganTokenRequest
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganDetailResponse
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganListResponse
import team.mozu.dsm.domain.organ.model.Organ
import java.util.*

@Tag(name = "Organ", description = "기관 관련 API")
interface OrganApiDocument {

    @Operation(
        summary = "[내부] 기관 생성",
        description = "새로운 기관을 생성합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "기관 생성 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = Organ::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
        ),
        ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 기관"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun createOrgan(
        @RequestBody @Valid
        request: CreateOrganRequest
    ): Organ

    @Operation(
        summary = "기관 토큰 재발급",
        description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "기관 토큰 재발급 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = TokenResponse::class)
                )
            ]
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
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun reissueOrganToken(
        @RequestBody @Valid
        request: ReissueOrganTokenRequest
    ): TokenResponse

    @Operation(
        summary = "기관 로그인",
        description = "기관 관리자 로그인을 수행합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = TokenResponse::class)
                )
            ]
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
            responseCode = "404",
            description = "존재하지 않는 기관"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun login(
        @RequestBody @Valid
        request: LoginOrganRequest
    ): TokenResponse

    @Operation(
        summary = "기관 상세 조회",
        description = "지정된 ID의 기관 상세 정보를 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "기관 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = OrganDetailResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 기관"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun queryOrganDetail(
        @PathVariable id: UUID
    ): OrganDetailResponse

    @Operation(
        summary = "기관 목록 조회",
        description = "등록된 모든 기관의 목록을 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "기관 목록 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = OrganListResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun queryOrganInventory(): List<OrganListResponse>
}
