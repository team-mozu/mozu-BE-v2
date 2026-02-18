package team.mozu.dsm.adapter.`in`.item.dto.response

import team.mozu.dsm.domain.item.model.Item

data class ItemResponse(
    val itemId: Int,
    val itemName: String
) {
    companion object {
        fun from(item: Item): ItemResponse {
            return ItemResponse(
                itemId = item.id!!,
                itemName = item.itemName
            )
        }
    }
}
