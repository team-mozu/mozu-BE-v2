package team.mozu.dsm.global.error

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter
import team.mozu.dsm.global.error.exception.ErrorCode
import team.mozu.dsm.global.error.exception.MozuException

class GlobalExceptionFilter(
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: MozuException) {
            val errorCode: ErrorCode = e.errorCode
            writeErrorResponse(
                response,
                errorCode.httpStatus.value(), // HttpStatus → Int 변환
                ErrorResponse.of(errorCode, errorCode.message)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            writeErrorResponse(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
            )
        }
    }

    private fun writeErrorResponse(
        response: HttpServletResponse,
        statusCode: Int,
        errorResponse: ErrorResponse
    ) {
        response.status = statusCode
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        objectMapper.writeValue(response.writer, errorResponse)
    }
}
