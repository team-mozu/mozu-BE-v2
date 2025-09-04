package team.mozu.dsm.adapter.`in`.item

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.mozu.dsm.adapter.`in`.item.dto.request.ItemRequest
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import team.mozu.dsm.adapter.out.item.persistence.mapper.ItemMapper
import team.mozu.dsm.application.port.`in`.item.CreateItemUseCase
import team.mozu.dsm.application.port.`in`.item.UpdateItemUseCase
import team.mozu.dsm.application.port.`in`.item.QueryItemAllUseCase
import team.mozu.dsm.application.port.`in`.item.QueryItemDetailUseCase
import team.mozu.dsm.application.port.`in`.item.DeleteItemUseCase
import java.util.*

@RestController
@RequestMapping("item")
class ItemWebAdapter(
    private val createItemUseCase: CreateItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val queryItemDetailUseCase: QueryItemDetailUseCase,
    private val queryItemAllUseCase: QueryItemAllUseCase,
    private val itemMapper: ItemMapper,
    private val deleteItemUseCase: DeleteItemUseCase
) {

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody @Valid
        request: ItemRequest
    ): ItemResponse {
        return createItemUseCase.create(request)
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid
        request: ItemRequest
    ): ItemResponse {
        return updateItemUseCase.update(id, request)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun queryDetail(
        @PathVariable id: UUID
    ): ItemResponse {
        val item = queryItemDetailUseCase.queryDetail(id)
        return itemMapper.toResponse(item)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun queryAll(): List<ItemResponse> {
        return queryItemAllUseCase.queryAll()
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: UUID
    ) {
        deleteItemUseCase.delete(id)
    }
}
