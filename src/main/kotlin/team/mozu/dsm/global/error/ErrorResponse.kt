package team.mozu.dsm.global.error

import org.springframework.http.HttpStatus
import team.mozu.dsm.global.error.exception.ErrorCode
import java.time.LocalDateTime

data class ErrorResponse(
    val message: String,
    val status: Int,
    val timestamp: LocalDateTime,
    val description: String?
) {
    companion object {
        fun of(errorCode: ErrorCode, description: String?): ErrorResponse =
            ErrorResponse(
                message = errorCode.message,
                status = errorCode.httpStatus.value(), // 여기서 숫자로 변환
                timestamp = LocalDateTime.now(),
                description = description
            )

        fun of(httpStatus: HttpStatus, message: String? = null, description: String? = null): ErrorResponse =
            ErrorResponse(
                message = message ?: httpStatus.reasonPhrase,
                status = httpStatus.value(),
                timestamp = LocalDateTime.now(),
                description = description
            )

        fun of(statusCode: Int, description: String?): ErrorResponse =
            ErrorResponse(
                message = description ?: "",
                status = statusCode,
                timestamp = LocalDateTime.now(),
                description = description
            )
    }
}
