package team.mozu.dsm.global.error

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
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
                errorCode.status,
                ErrorResponse.of(errorCode, errorCode.message)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            writeErrorResponse(
                response,
                response.status,
                ErrorResponse.of(response.status, e.message)
            )
        }
    }

    private fun writeErrorResponse(
        response: HttpServletResponse,
        statusCode: Int,
        errorResponse: ErrorResponse
    ) {
        response.status = statusCode
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        objectMapper.writeValue(response.writer, errorResponse)
    }
}
