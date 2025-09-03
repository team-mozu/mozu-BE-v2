package team.mozu.dsm.application.exception.lesson

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object MaxInvestmentRoundReachedException : MozuException(ErrorCode.MAX_INVESTMENT_ROUND_REACHED)
