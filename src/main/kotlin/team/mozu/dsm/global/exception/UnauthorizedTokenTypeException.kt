package team.mozu.dsm.global.exception

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object UnauthorizedTokenTypeException : MozuException(ErrorCode.UNAUTHORIZED_TOKEN_TYPE)
