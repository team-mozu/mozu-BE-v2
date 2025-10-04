package team.mozu.dsm.application.port.out.sse

interface PublishSsePort {

    fun publishTo(clientId: String, eventName: String, data: Any)

    fun publishToAll(eventName: String, data: Any)
}
