package team.mozu.dsm.application.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object LessonCreatedAtNotFoundException : MozuException(ErrorCode.LESSON_CREATED_AT_NOT_FOUND)
