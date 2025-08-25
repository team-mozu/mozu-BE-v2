package team.mozu.dsm.application.port.`in`.sse

interface PublishToAllSseUseCase {

    fun publishToAll(eventName: String, data: Any)
}
