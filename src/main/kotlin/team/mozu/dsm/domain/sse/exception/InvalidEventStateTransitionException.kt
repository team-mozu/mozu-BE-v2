package team.mozu.dsm.domain.sse.exception

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object InvalidEventStateTransitionException : MozuException(ErrorCode.SSE_INVALID_STATE_TRANSITION)
