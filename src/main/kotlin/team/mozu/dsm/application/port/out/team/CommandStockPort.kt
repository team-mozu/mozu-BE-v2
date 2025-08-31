package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.domain.team.model.Stock
import java.util.UUID

interface CommandStockPort {

    fun saveAll(stocks: List<Stock>)

    fun deleteById(id: UUID)
}
