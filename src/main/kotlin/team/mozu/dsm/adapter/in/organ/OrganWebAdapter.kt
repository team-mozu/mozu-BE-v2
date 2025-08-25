package team.mozu.dsm.adapter.`in`.organ

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.mozu.dsm.adapter.`in`.organ.dto.request.CreateOrganCommand
import team.mozu.dsm.application.service.organ.CreateOrganService
import team.mozu.dsm.domain.organ.model.Organ

@RestController
@RequestMapping("/organ")
class OrganWebAdapter(
    private val createOrganService: CreateOrganService
) {

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrgan(
        @RequestBody @Valid
        command: CreateOrganCommand
    ): Organ {
        return createOrganService.create(command)
    }
}
