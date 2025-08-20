package team.mozu.dsm.global.exception.s3

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object FailedUploadException : MozuException(ErrorCode.IMAGE_UPLOAD_FAILED) {
}
