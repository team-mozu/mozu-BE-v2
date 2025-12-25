package team.mozu.dsm.adapter.`in`.organ

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PatchMapping
import team.mozu.dsm.adapter.`in`.auth.dto.response.TokenResponse
import team.mozu.dsm.adapter.`in`.organ.dto.request.CreateOrganRequest
import team.mozu.dsm.adapter.`in`.organ.dto.request.ReissueOrganTokenRequest
import team.mozu.dsm.application.port.`in`.organ.ReissueOrganTokenUseCase
import team.mozu.dsm.adapter.`in`.organ.dto.request.LoginOrganRequest
import team.mozu.dsm.adapter.`in`.organ.dto.response.MyOrganResponse
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganDetailResponse
import team.mozu.dsm.adapter.`in`.organ.dto.response.OrganListResponse
import team.mozu.dsm.application.port.`in`.organ.CreateOrganUseCase
import team.mozu.dsm.application.port.`in`.organ.GetMyOrganUseCase
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
    private val queryOrganInventoryUseCase: QueryOrganInventoryUseCase,
    private val getMyOrganUseCase: GetMyOrganUseCase
) : OrganApiDocument {

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    override fun createOrgan(
        @RequestBody @Valid
        request: CreateOrganRequest
    ): Organ {
        return createOrganUseCase.execute(request)
    }

    @PatchMapping("/token/reissue")
    @ResponseStatus(HttpStatus.OK)
    override fun reissueOrganToken(
        @RequestBody @Valid
        request: ReissueOrganTokenRequest
    ): TokenResponse {
        return reissueOrganTokenUseCase.execute(request)
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    override fun login(
        @RequestBody @Valid
        request: LoginOrganRequest
    ): TokenResponse {
        return loginOrganUseCase.execute(request)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    override fun queryOrganDetail(@PathVariable id: UUID): OrganDetailResponse {
        return queryOrganDetailUseCase.execute(id)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun queryOrganInventory(): List<OrganListResponse> {
        return queryOrganInventoryUseCase.execute()
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    override fun getMyOrgan(): MyOrganResponse {
        return getMyOrganUseCase.execute()
    }
}
