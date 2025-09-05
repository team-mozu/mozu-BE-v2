package team.mozu.dsm.application.exception.article

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object ArticleNotFoundException : MozuException(ErrorCode.ARTICLE_NOT_FOUND)
