package team.mozu.dsm.adapter.`in`.organ

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.mozu.dsm.adapter.`in`.organ.dto.request.CreateOrganRequest
import team.mozu.dsm.application.port.`in`.organ.CreateOrganUseCase
import team.mozu.dsm.domain.organ.model.Organ

@RestController
@RequestMapping("/organ")
class OrganWebAdapter(
    private val createOrganUseCase: CreateOrganUseCase
) {

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrgan(
        @RequestBody @Valid
        command: CreateOrganRequest
    ): Organ {
        return createOrganUseCase.create(command)
    }
}
