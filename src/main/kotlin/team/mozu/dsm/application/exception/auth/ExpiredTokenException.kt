package team.mozu.dsm.application.exception.auth

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object ExpiredTokenException : MozuException(ErrorCode.EXPIRED_TOKEN)
