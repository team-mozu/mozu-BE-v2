package team.mozu.dsm.application.exception.organ

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object OrganNotFoundException : MozuException(ErrorCode.ORGAN_NOT_FOUND)
