package team.mozu.dsm.adapter.`in`.item

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.mozu.dsm.adapter.`in`.item.dto.request.ItemRequest
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import team.mozu.dsm.application.port.`in`.item.CreateItemUseCase

@RestController
@RequestMapping("item")
class ItemWebAdapter (
    private val createItemUseCase: CreateItemUseCase
){

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun create (
        @RequestBody @Valid
        request: ItemRequest
    ) : ItemResponse {
        return createItemUseCase.create(request)
    }
}
