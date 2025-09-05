package team.mozu.dsm.domain.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object InvalidLessonRoundException : MozuException(ErrorCode.INVALID_LESSON_ROUND)
