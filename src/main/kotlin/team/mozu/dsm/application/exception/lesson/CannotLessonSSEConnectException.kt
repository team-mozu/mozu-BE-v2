package team.mozu.dsm.application.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object CannotLessonSSEConnectException : MozuException(ErrorCode.CANNOT_LESSON_SSE_CONNECT)
