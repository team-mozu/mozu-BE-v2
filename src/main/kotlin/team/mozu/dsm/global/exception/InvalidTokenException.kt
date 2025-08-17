package team.mozu.dsm.global.exception

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object InvalidTokenException : MozuException(ErrorCode.INVALID_TOKEN)
