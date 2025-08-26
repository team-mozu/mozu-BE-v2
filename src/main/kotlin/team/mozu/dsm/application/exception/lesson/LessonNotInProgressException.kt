package team.mozu.dsm.application.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object LessonNotInProgressException : MozuException(ErrorCode.LESSON_NOT_IN_PROGRESS)
