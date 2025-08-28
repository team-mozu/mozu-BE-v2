package team.mozu.dsm.application.exception.team

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object StockNotOwnedException : MozuException(ErrorCode.STOCK_NOT_OWNED)
