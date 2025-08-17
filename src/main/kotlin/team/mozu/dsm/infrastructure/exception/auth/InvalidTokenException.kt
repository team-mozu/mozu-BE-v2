package team.mozu.dsm.infrastructure.exception.auth

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object InvalidTokenException : MozuException(ErrorCode.INVALID_TOKEN)
