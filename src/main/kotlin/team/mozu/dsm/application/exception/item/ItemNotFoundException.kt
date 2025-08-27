package team.mozu.dsm.application.exception.item

import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

object ItemNotFoundException : MozuException(ErrorCode.ITEM_NOT_FOUND)
