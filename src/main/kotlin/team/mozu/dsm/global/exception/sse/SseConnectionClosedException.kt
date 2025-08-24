package team.mozu.dsm.global.exception.sse

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object SseConnectionClosedException : MozuException(ErrorCode.SSE_CONNECTION_CLOSED)
