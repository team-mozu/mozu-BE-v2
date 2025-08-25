package team.mozu.dsm.adapter.`in`.organ.dto.request

data class CreateOrganCommand(
    val organName: String,
    val organCode: String,
    val password: String
)
