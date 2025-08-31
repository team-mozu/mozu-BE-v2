package team.mozu.dsm.application.port.out.organ

import team.mozu.dsm.domain.organ.model.Organ
import java.util.UUID

interface QueryOrganPort {

    fun findByOrganCode(organCode: String): Organ?

    fun findModelById(organId: UUID): Organ?
}
