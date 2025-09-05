package team.mozu.dsm.application.exception.team

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object InsufficientStockQuantityException : MozuException(ErrorCode.STOCK_QUANTITY_INSUFFICIENT)
