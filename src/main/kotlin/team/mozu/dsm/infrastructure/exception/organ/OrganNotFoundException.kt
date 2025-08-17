package team.mozu.dsm.infrastructure.exception.organ

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object OrganNotFoundException: MozuException(ErrorCode.ORGAN_NOT_FOUND) {
}
