package team.mozu.dsm.adapter.`in`.item

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.DeleteMapping
import team.mozu.dsm.adapter.`in`.item.dto.request.ItemRequest
import team.mozu.dsm.adapter.`in`.item.dto.request.UpdateItemRequest
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemQueryResponse
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemDetailResponse
import team.mozu.dsm.application.port.`in`.item.CreateItemUseCase
import team.mozu.dsm.application.port.`in`.item.UpdateItemUseCase
import team.mozu.dsm.application.port.`in`.item.QueryItemsUseCase
import team.mozu.dsm.application.port.`in`.item.QueryItemDetailUseCase
import team.mozu.dsm.application.port.`in`.item.DeleteItemUseCase
import team.mozu.dsm.global.document.item.ItemApiDocument

@RestController
@RequestMapping("/item")
class ItemWebAdapter(
    private val createItemUseCase: CreateItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val queryItemDetailUseCase: QueryItemDetailUseCase,
    private val queryItemsUseCase: QueryItemsUseCase,
    private val deleteItemUseCase: DeleteItemUseCase
) : ItemApiDocument {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    override fun create(
        @ModelAttribute @Valid
        request: ItemRequest
    ): ItemDetailResponse = createItemUseCase.execute(request)

    @PatchMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.OK)
    override fun update(
        @PathVariable id: Int,
        @ModelAttribute @Valid
        request: UpdateItemRequest
    ): ItemDetailResponse = updateItemUseCase.execute(id, request)

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    override fun queryDetail(
        @PathVariable id: Int
    ): ItemDetailResponse = queryItemDetailUseCase.execute(id)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun queryAll(): List<ItemQueryResponse> =
        queryItemsUseCase.execute()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun delete(
        @PathVariable id: Int
    ) = deleteItemUseCase.execute(id)
}
