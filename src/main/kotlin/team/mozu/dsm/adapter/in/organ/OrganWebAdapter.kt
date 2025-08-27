package team.mozu.dsm.adapter.`in`.organ

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.mozu.dsm.adapter.`in`.auth.dto.response.TokenResponse
import team.mozu.dsm.adapter.`in`.organ.dto.request.CreateOrganRequest
import team.mozu.dsm.adapter.`in`.organ.dto.request.ReissueOrganTokenRequest
import team.mozu.dsm.application.port.`in`.organ.ReissueOrganTokenUseCase
import team.mozu.dsm.adapter.`in`.organ.dto.request.LoginOrganRequest
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganDetailResponse
import team.mozu.dsm.application.port.`in`.organ.CreateOrganUseCase
import team.mozu.dsm.application.port.`in`.organ.LoginOrganUseCase
import team.mozu.dsm.application.port.`in`.organ.QueryOrganDetailUseCase
import team.mozu.dsm.domain.organ.model.Organ
import java.util.UUID

@RestController
@RequestMapping("/organ")
class OrganWebAdapter(
    private val createOrganUseCase: CreateOrganUseCase,
    private val reissueOrganTokenUseCase: ReissueOrganTokenUseCase,
    private val loginOrganUseCase: LoginOrganUseCase,
    private val queryOrganDetailUseCase: QueryOrganDetailUseCase
) {

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrgan(
        @RequestBody @Valid
        request: CreateOrganRequest
    ): Organ {
        return createOrganUseCase.create(request)
    }

    @PatchMapping("/token/reissue")
    @ResponseStatus(HttpStatus.OK)
    fun reissueOrganToken(
        @RequestBody @Valid
        request: ReissueOrganTokenRequest
    ): TokenResponse {
        return reissueOrganTokenUseCase.reissue(request)
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    fun login(
        @RequestBody @Valid
        request: LoginOrganRequest
    ): TokenResponse {
        return loginOrganUseCase.login(request)
    }

    @GetMapping("/{organId}")
    @ResponseStatus(HttpStatus.OK)
    fun queryOrganDetail(@PathVariable organId: UUID): OrganDetailResponse {
        return queryOrganDetailUseCase.queryOrganDetail(organId)
    }
}
