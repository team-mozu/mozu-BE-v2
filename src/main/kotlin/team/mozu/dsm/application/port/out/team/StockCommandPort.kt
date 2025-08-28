package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.domain.team.model.Stock
import java.util.UUID

interface StockCommandPort {

    fun save(stock: Stock): Stock

    fun saveAll(stocks: List<Stock>): List<Stock>

    fun deleteById(id: UUID)
}
