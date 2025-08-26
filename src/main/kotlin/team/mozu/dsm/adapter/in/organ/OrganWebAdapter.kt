package team.mozu.dsm.adapter.`in`.organ

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.mozu.dsm.adapter.`in`.auth.dto.response.TokenResponse
import team.mozu.dsm.adapter.`in`.organ.dto.request.CreateOrganRequest
import team.mozu.dsm.adapter.`in`.organ.dto.request.LoginOrganRequest
import team.mozu.dsm.application.port.`in`.organ.CreateOrganUseCase
import team.mozu.dsm.application.port.`in`.organ.LoginOrganUseCase
import team.mozu.dsm.domain.organ.model.Organ

@RestController
@RequestMapping("/organ")
class OrganWebAdapter(
    private val createOrganUseCase: CreateOrganUseCase,
    private val loginOrganUseCase: LoginOrganUseCase
) {

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrgan(
        @RequestBody @Valid
        request: CreateOrganRequest
    ): Organ {
        return createOrganUseCase.create(request)
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    fun login(
        @RequestBody @Valid
        request: LoginOrganRequest
    ): TokenResponse {
        return loginOrganUseCase.login(request)
    }
}
