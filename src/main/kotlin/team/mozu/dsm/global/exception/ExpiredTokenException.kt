package team.mozu.dsm.global.exception

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object ExpiredTokenException : MozuException(ErrorCode.EXPIRED_TOKEN)
