package team.mozu.dsm.application.port.`in`.organ

import team.mozu.dsm.adapter.`in`.organ.dto.request.CreateOrganCommand
import team.mozu.dsm.domain.organ.model.Organ

interface CreateOrganUseCase {

    fun create(command: CreateOrganCommand): Organ
}
