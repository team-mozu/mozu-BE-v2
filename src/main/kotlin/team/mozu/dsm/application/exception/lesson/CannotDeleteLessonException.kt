package team.mozu.dsm.application.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object CannotDeleteLessonException: MozuException(ErrorCode.CANNOT_DELETE_LESSON)
