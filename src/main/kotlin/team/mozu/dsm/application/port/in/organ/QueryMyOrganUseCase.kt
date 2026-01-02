package team.mozu.dsm.application.port.`in`.organ

import team.mozu.dsm.adapter.`in`.organ.dto.response.MyOrganResponse

interface QueryMyOrganUseCase {

    fun execute(): MyOrganResponse
}
