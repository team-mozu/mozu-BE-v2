package team.mozu.dsm.domain.sse.model

enum class SseEventType(
    val eventName: String
) {
    // lesson
    NEXT_LESSON_EVENT("CLASS_NEXT_INV_START"),
    START_LESSON_EVENT("CLASS_START")
}
