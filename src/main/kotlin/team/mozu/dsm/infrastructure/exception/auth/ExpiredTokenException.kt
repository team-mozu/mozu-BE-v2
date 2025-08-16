package team.mozu.dsm.infrastructure.exception.auth

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

class ExpiredTokenException: MozuException
    (ErrorCode.EXPIRED_TOKEN) {
        companion object {
            val EXCEPTION = ExpiredTokenException()
        }
}
