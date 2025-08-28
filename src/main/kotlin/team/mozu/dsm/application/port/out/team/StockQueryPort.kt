package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.domain.team.model.Stock
import java.util.UUID

interface StockQueryPort {

    fun findByTeamIdAndItemId(teamId: UUID, itemId: UUID): Stock?

    fun findAllByTeamId(teamId: UUID): List<Stock>
}
