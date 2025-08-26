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
    UNAUTHORIZED_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "Unauthorized Token Type"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "RefreshToken Not Found"),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "Password Mismatch"),

    // s3
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Image Not Found"),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Image Upload Failed"),
    IMAGE_FAILED_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "Image Delete Failed"),

    // SSE
    SSE_CONNECTION_CLOSED(HttpStatus.GONE, "SSE Connection Closed"),
    INVALID_SSE_STATE(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid SSE State"),
    INVALID_SSE_DATA(HttpStatus.BAD_REQUEST, "Invalid SSE Data"),
    UNKNOWN_SSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown SSE Error"),
    LESSON_CREATED_AT_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "Created at Not Found"),

    // organ
    ORGAN_NOT_FOUND(HttpStatus.NOT_FOUND, "Organ Not Found"),

    //lesson
    LESSON_NUM_NOT_FOUND(HttpStatus.NOT_FOUND, "Lesson Num Not Found"),
    LESSON_NOT_FOUND(HttpStatus.NOT_FOUND, "Lesson Not Found"),
    LESSON_NOT_IN_PROGRESS(HttpStatus.UNPROCESSABLE_ENTITY, "Lesson Not In Progress"),
    LESSON_DELETED(HttpStatus.BAD_REQUEST, "Lesson Deleted"),
    TEAM_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "Team Name Required"),

    // general
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error") ;
}
