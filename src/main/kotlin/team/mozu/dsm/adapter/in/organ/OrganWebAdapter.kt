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
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganListResponse
import team.mozu.dsm.application.port.`in`.organ.CreateOrganUseCase
import team.mozu.dsm.application.port.`in`.organ.LoginOrganUseCase
import team.mozu.dsm.application.port.`in`.organ.QueryOrganDetailUseCase
import team.mozu.dsm.application.port.`in`.organ.QueryOrganInventoryUseCase
import team.mozu.dsm.domain.organ.model.Organ
import team.mozu.dsm.global.document.organ.OrganApiDocument
import java.util.UUID

@RestController
@RequestMapping("/organ")
class OrganWebAdapter(
    private val createOrganUseCase: CreateOrganUseCase,
    private val reissueOrganTokenUseCase: ReissueOrganTokenUseCase,
    private val loginOrganUseCase: LoginOrganUseCase,
    private val queryOrganDetailUseCase: QueryOrganDetailUseCase,
    private val queryOrganInventoryUseCase: QueryOrganInventoryUseCase
) : OrganApiDocument {

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    override fun createOrgan(
        @RequestBody @Valid
        request: CreateOrganRequest
    ): Organ {
        return createOrganUseCase.create(request)
    }

    @PatchMapping("/token/reissue")
    @ResponseStatus(HttpStatus.OK)
    override fun reissueOrganToken(
        @RequestBody @Valid
        request: ReissueOrganTokenRequest
    ): TokenResponse {
        return reissueOrganTokenUseCase.reissue(request)
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    override fun login(
        @RequestBody @Valid
        request: LoginOrganRequest
    ): TokenResponse {
        return loginOrganUseCase.login(request)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    override fun queryOrganDetail(@PathVariable id: UUID): OrganDetailResponse {
        return queryOrganDetailUseCase.queryOrganDetail(id)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun queryOrganInventory(): List<OrganListResponse> {
        return queryOrganInventoryUseCase.queryOrganInventory()
    }
}
