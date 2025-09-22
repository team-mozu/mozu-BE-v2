package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.domain.team.model.Stock
import java.util.UUID

interface QueryStockPort {

    fun findByTeamIdAndItemId(teamId: UUID, itemId: Int): Stock?

    fun findAllByTeamId(teamId: UUID): List<Stock>
}
