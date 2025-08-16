package team.mozu.dsm.global.error.exception

abstract class MozuException(
    val errorCode: ErrorCode
) : RuntimeException()
