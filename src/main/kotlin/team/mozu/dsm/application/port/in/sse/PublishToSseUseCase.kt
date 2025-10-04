package team.mozu.dsm.application.port.`in`.sse

interface PublishToSseUseCase {

    fun publishTo(clientId: String, eventName: String, data: Any)
}
