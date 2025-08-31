package team.mozu.dsm.application.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object LessonItemNotFoundException : MozuException(ErrorCode.LESSON_ITEM_NOT_FOUND)
