package team.mozu.dsm.application.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object LessonNumNotFoundException : MozuException(ErrorCode.LESSON_NUM_NOT_FOUND)
