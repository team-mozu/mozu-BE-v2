package team.mozu.dsm.application.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object CannotUpdateLessonException : MozuException(ErrorCode.CANNOT_UPDATE_LESSON)
