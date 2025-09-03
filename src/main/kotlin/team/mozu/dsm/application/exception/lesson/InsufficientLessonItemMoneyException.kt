package team.mozu.dsm.application.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object InsufficientLessonItemMoneyException : MozuException(ErrorCode.INSUFFICIENT_LESSON_ITEM_MONEY)
