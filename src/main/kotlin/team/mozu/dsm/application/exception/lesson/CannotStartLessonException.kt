package team.mozu.dsm.application.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object CannotStartLessonException : MozuException(ErrorCode.CANNOT_START_LESSON)
