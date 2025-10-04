package team.mozu.dsm.global.exception.sse

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object UnknownSseErrorException : MozuException(ErrorCode.UNKNOWN_SSE_ERROR)
