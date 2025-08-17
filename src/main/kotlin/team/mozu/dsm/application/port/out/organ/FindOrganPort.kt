package team.mozu.dsm.application.port.out.organ

import team.mozu.dsm.domain.organ.model.Organ

interface FindOrganPort {
    fun findByOrganCode(organCode: String): Organ?
}
