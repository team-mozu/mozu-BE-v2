package team.mozu.dsm.application.port.out.auth

import team.mozu.dsm.domain.organ.model.Organ

interface SecurityPort {

    fun getCurrentOrgan(): Organ
}
