package team.mozu.dsm.adapter.`in`.item

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.mozu.dsm.adapter.`in`.item.dto.request.ItemRequest
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import team.mozu.dsm.application.port.`in`.item.CreateItemUseCase
import team.mozu.dsm.application.port.`in`.item.UpdateItemUseCase
import team.mozu.dsm.application.port.`in`.item.DeleteItemUseCase
import java.util.*

@RestController
@RequestMapping("item")
class ItemWebAdapter (
    private val createItemUseCase: CreateItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase
){

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun create (
        @RequestBody @Valid
        request: ItemRequest
    ) : ItemResponse {
        return createItemUseCase.create(request)
    }
    
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid request: ItemRequest
    ): ItemResponse {
        return updateItemUseCase.update(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: UUID
    ) {
        deleteItemUseCase.delete(id)
    }
}
