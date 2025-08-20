package team.mozu.dsm.global.error.exception
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ErrorCode(
    val httpStatus: HttpStatus,
    val message: String
) {
    // jwt
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Expired Token"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token"),

    // s3
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Image Not Found"),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Image Upload Failed"),
    IMAGE_FAILED_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "Image Delete Failed"),

    // organ
    ORGAN_NOT_FOUND(HttpStatus.NOT_FOUND, "Organ Not Found"),

    // general
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error") ;
}
