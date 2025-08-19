package team.mozu.dsm.global.exception.s3

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object FailedDeleteException : MozuException(ErrorCode.FAILED_DELETE) {
}
