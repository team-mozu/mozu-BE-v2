package team.mozu.dsm.global.error

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

@RestControllerAdvice
class GlobalExceptionHandler {
    // 비즈니스 로직 예외
    @ExceptionHandler(MozuException::class)
    fun handleTeam8Exception(e: MozuException): ResponseEntity<ErrorResponse> {
        val errorCode = e.errorCode
        val body = ErrorResponse.of(errorCode, errorCode.message)
        e.printStackTrace()
        return ResponseEntity.status(errorCode.status).body(body)
    }

    // validation 예외
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.BAD_REQUEST
        val body = ErrorResponse.of(errorCode, errorCode.message)
        e.printStackTrace()
        return ResponseEntity.status(errorCode.status).body(body)
    }

    // 그 외 예외
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR
        val body = ErrorResponse.of(errorCode, e.message ?: errorCode.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }
}
