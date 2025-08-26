package team.mozu.dsm.application.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object TeamNameRequiredException : MozuException(ErrorCode.TEAM_NAME_REQUIRED)
