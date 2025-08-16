package team.mozu.dsm.infrastructure.exception.organ

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

class OrganNotFoundException : MozuException
(ErrorCode.ORGAN_NOT_FOUND) {
    companion object {
        @JvmField
        val EXCEPTION = OrganNotFoundException()
    }
}
