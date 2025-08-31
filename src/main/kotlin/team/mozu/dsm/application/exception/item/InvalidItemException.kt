package team.mozu.dsm.application.exception.item

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object InvalidItemException : MozuException(ErrorCode.INVALID_INVESTMENT_ITEM)
