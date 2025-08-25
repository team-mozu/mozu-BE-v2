package team.mozu.dsm.application.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object LessonIdNotFoundException : MozuException(ErrorCode.LESSON_ID_NOT_FOUND)
