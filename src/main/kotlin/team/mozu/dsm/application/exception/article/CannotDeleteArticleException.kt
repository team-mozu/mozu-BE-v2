package team.mozu.dsm.application.exception.article

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object CannotDeleteArticleException : MozuException(ErrorCode.CANNOT_DELETE_ARTICLE)
