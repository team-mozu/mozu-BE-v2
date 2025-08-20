package team.mozu.dsm.global.exception.s3

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object ImageNotFoundException : MozuException(ErrorCode.IMAGE_NOT_FOUND) {
}
