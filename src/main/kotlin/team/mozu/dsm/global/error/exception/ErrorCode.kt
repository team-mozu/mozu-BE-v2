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
    ORGAN_ACCESS_DENIED(HttpStatus.FORBIDDEN, "Organ Access Denied"),

    //item
    INVALID_INVESTMENT_ITEM(HttpStatus.BAD_REQUEST, "Invalid Investment Item"),
    ITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "Item Not Found"),
    ITEM_DELETED(HttpStatus.BAD_REQUEST, "Item Deleted"),
    INSUFFICIENT_LESSON_ITEM_MONEY(HttpStatus.BAD_REQUEST, "Insufficient Lesson Item Money"),

    //lesson
    LESSON_NUM_NOT_FOUND(HttpStatus.NOT_FOUND, "Lesson Num Not Found"),
    LESSON_NOT_FOUND(HttpStatus.NOT_FOUND, "Lesson Not Found"),
    LESSON_NOT_IN_PROGRESS(HttpStatus.UNPROCESSABLE_ENTITY, "Lesson Not In Progress"),
    LESSON_DELETED(HttpStatus.BAD_REQUEST, "Lesson Deleted"),
    TEAM_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "Team Name Required"),
    LESSON_ITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "Lesson Item Not Found"),
    INVALID_LESSON_ROUND(HttpStatus.BAD_REQUEST, "Invalid Lesson Round"),
    CANNOT_UPDATE_LESSON(HttpStatus.FORBIDDEN, "Can't update Lesson"),
    CANNOT_DELETE_LESSON(HttpStatus.FORBIDDEN, "Can't delete Lesson"),
    MAX_INVESTMENT_ROUND_REACHED(HttpStatus.CONFLICT, "Max Investment Round Reached"),
    UNAUTHORIZED_LESSON_ACCESS(HttpStatus.FORBIDDEN, "Unauthorized"),
    CANNOT_STAR_LESSON(HttpStatus.FORBIDDEN, "Can't star Lesson"),
    CANNOT_END_LESSON(HttpStatus.FORBIDDEN, "Can't end Lesson"),
    CANNOT_NEXT_LESSON(HttpStatus.FORBIDDEN, "Can't next Lesson"),
    CANNOT_START_LESSON(HttpStatus.FORBIDDEN, "Can't start Lesson"),

    //team
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "Team Not Found"),
    STOCK_QUANTITY_INSUFFICIENT(HttpStatus.CONFLICT, "Stock Quantity Insufficient"),
    STOCK_NOT_OWNED(HttpStatus.CONFLICT, "Stock Not Owned"),
    INSUFFICIENT_CASH(HttpStatus.BAD_REQUEST, "Insufficient Cash"),

    // article
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Article Not Found"),

    // general
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
}
