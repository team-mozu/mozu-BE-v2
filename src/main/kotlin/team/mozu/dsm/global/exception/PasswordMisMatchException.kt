package team.mozu.dsm.global.exception

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object PasswordMisMatchException : MozuException(ErrorCode.PASSWORD_MISMATCH)
