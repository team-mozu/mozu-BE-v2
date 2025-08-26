package team.mozu.dsm.global.exception

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object RefreshTokenNotFoundException : MozuException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
