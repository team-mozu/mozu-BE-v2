package team.mozu.dsm.global.error.exception
import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ErrorCode(
    val status: Int,
    val message: String
) {

    // general
    BAD_REQUEST(400, "front fault"),
    INTERNAL_SERVER_ERROR(500, "server fault");
}
