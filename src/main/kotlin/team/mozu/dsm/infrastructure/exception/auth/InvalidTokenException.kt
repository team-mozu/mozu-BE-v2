package team.mozu.dsm.infrastructure.exception.auth

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

class InvalidTokenException: MozuException
    (ErrorCode.INVALID_TOKEN) {
        companion object {
            val EXCEPTION = InvalidTokenException()
        }
}
