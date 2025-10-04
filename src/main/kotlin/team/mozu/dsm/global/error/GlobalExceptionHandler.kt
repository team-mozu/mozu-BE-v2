package team.mozu.dsm.global.error

import jakarta.persistence.EntityNotFoundException
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

@RestControllerAdvice
class GlobalExceptionHandler {

    // 비즈니스 로직 예외
    @ExceptionHandler(MozuException::class)
    fun handleMozuException(e: MozuException): ResponseEntity<ErrorResponse> {
        val errorCode = e.errorCode
        return ResponseEntity.status(errorCode.httpStatus)
            .body(ErrorResponse.of(errorCode, errorCode.message))
    }

    // DTO(@RequestBody) 필드 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.BAD_REQUEST
        return ResponseEntity.status(errorCode.httpStatus)
            .body(ErrorResponse.of(errorCode, "Validation Failed"))
    }

    // @PathVariable 검증 실패
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(e: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.BAD_REQUEST
        return ResponseEntity.status(errorCode.httpStatus)
            .body(ErrorResponse.of(errorCode, "Constraint Violation"))
    }

    // 잘못된 인자
    @ExceptionHandler(IllegalArgumentException::class, IllegalStateException::class)
    fun handleIllegalArgs(e: RuntimeException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.BAD_REQUEST
        return ResponseEntity.status(errorCode.httpStatus)
            .body(ErrorResponse.of(errorCode, e.message ?: errorCode.message))
    }

    // JPA / DB 관련 (무결성 위반)
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(e: org.springframework.dao.DataIntegrityViolationException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.BAD_REQUEST
        return ResponseEntity.status(errorCode.httpStatus)
            .body(ErrorResponse.of(errorCode, "Invalid DB State"))
    }

    // 엔티티 못 찾음
    @ExceptionHandler(EntityNotFoundException::class, NoSuchElementException::class)
    fun handleNotFound(e: RuntimeException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.ARTICLE_NOT_FOUND
        return ResponseEntity.status(errorCode.httpStatus)
            .body(ErrorResponse.of(errorCode, e.message ?: errorCode.message))
    }

    // 권한 문제
    @ExceptionHandler(AccessDeniedException::class, SecurityException::class)
    fun handleForbidden(e: RuntimeException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.ORGAN_ACCESS_DENIED
        return ResponseEntity.status(errorCode.httpStatus)
            .body(ErrorResponse.of(errorCode, e.message ?: errorCode.message))
    }

    // 그 외 예기치 못한 에러만 500
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        e.printStackTrace()
        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR
        return ResponseEntity.status(errorCode.httpStatus)
            .body(ErrorResponse.of(errorCode, e.message ?: errorCode.message))
    }
}
