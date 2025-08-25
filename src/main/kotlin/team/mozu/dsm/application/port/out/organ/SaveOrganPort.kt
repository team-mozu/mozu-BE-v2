package team.mozu.dsm.application.port.out.organ

import team.mozu.dsm.domain.organ.model.Organ

interface SaveOrganPort {

    fun save(organ: Organ): Organ
}
