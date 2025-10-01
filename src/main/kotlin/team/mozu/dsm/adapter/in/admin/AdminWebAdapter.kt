package team.mozu.dsm.adapter.`in`.admin

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.application.service.admin.AdminSseServiceImpl
import team.mozu.dsm.global.document.admin.AdminApiDocument
import java.util.UUID

@RestController
@RequestMapping("/admin")
class AdminWebAdapter(
    private val adminSseService: AdminSseServiceImpl
) : AdminApiDocument {

    @GetMapping(
        value = ["/sse"],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
    )
    @ResponseStatus(HttpStatus.OK)
    override fun connectAdminSSE(
        @RequestParam("lessonId") lessonId: UUID,
        authentication: org.springframework.security.core.Authentication
    ): SseEmitter {
        return adminSseService.connectAdminSSEWithAuth(lessonId, authentication)
    }
}
