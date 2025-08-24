package team.mozu.dsm.adapter.`in`.sse

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.application.port.`in`.sse.SseUseCase

@RestController
@RequestMapping("/sse")
class SseWebAdapter(
    private val sseUseCase: SseUseCase
) {

    @GetMapping("/subscribe")
    fun subscribe(
        @RequestBody @Valid
        clientId: String
    ): SseEmitter {
        return sseUseCase.subscribe(clientId)
    }
}
