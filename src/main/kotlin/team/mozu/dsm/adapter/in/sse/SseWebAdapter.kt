package team.mozu.dsm.adapter.`in`.sse

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.application.port.`in`.sse.SubscribeSseUseCase
import team.mozu.dsm.global.document.sse.SseApiDocument

@RestController
@RequestMapping("/sse")
class SseWebAdapter(
    private val subscribeSseUseCase: SubscribeSseUseCase
) : SseApiDocument {

    @GetMapping("/subscribe")
    override fun subscribe(
        @RequestParam
        clientId: String
    ): SseEmitter {
        return subscribeSseUseCase.subscribe(clientId)
    }
}
