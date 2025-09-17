package team.mozu.dsm.global.document.item

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import team.mozu.dsm.adapter.`in`.item.dto.request.ItemRequest
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import java.util.*

@Tag(name = "Item", description = "종목 관련 API")
interface ItemApiDocument {

    @Operation(
        summary = "새종목 생성",
        description = "새로운 종목을 생성합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "종목 생성 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ItemResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun create(
        @RequestBody @Valid
        request: ItemRequest
    ): ItemResponse

    @Operation(
        summary = "종목 수정",
        description = "지정된 ID의 종목 정보를 수정합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "종목 수정 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ItemResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 아이템"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid
        request: ItemRequest
    ): ItemResponse

    @Operation(
        summary = "종목 상세 조회",
        description = "지정된 ID의 종목 상세 정보를 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "종목 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ItemResponse::class)
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 아이템"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun queryDetail(
        @PathVariable id: UUID
    ): ItemResponse

    @Operation(
        summary = "종목 목록 조회",
        description = "등록된 모든 종목의 목록을 조회합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "종목 목록 조회 성공",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = ItemResponse::class))
                )
            ]
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun queryAll(): List<ItemResponse>

    @Operation(
        summary = "종목 삭제",
        description = "지정된 ID의 종목을 삭제합니다."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "204",
            description = "종목 삭제 성공"
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 아이템"
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    )
    fun delete(
        @PathVariable id: UUID
    )
}
