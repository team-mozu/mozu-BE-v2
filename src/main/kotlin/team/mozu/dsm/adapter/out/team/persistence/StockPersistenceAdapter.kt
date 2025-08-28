package team.mozu.dsm.adapter.out.team.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.item.persistence.repository.ItemRepository
import team.mozu.dsm.adapter.out.team.persistence.mapper.StockMapper
import team.mozu.dsm.adapter.out.team.persistence.repository.StockRepository
import team.mozu.dsm.adapter.out.team.persistence.repository.TeamRepository
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.out.team.StockCommandPort
import team.mozu.dsm.application.port.out.team.StockQueryPort
import team.mozu.dsm.domain.team.model.Stock
import java.util.*

@Component
class StockPersistenceAdapter(
    private val stockRepository: StockRepository,
    private val teamRepository: TeamRepository,
    private val itemRepository: ItemRepository,
    private val stockMapper: StockMapper
) : StockQueryPort, StockCommandPort {

    //--Query--//
    override fun findByTeamIdAndItemId(teamId: UUID, itemId: UUID): Stock? {
        return stockRepository.findByTeam_IdAndItem_Id(teamId, itemId)
            ?.let { stockMapper.toModel(it) }
    }

    override fun findAllByTeamId(teamId: UUID): List<Stock> {
        return stockRepository.findAllByTeam_Id(teamId)
            .map { stockMapper.toModel(it) }
    }

    //--Command--//
    override fun saveAll(stocks: List<Stock>) {
        stocks.forEach { stock ->
            val teamEntity = teamRepository.findById(stock.teamId)
                .orElseThrow { TeamNotFoundException }

            val itemEntity = itemRepository.findById(stock.itemId)
                .orElseThrow { ItemNotFoundException }

            val entity = stockRepository.findByTeam_IdAndItem_Id(stock.teamId, stock.itemId)?.apply {
                quantity = stock.quantity
                avgPurchasePrice = stock.avgPurchasePrice
                buyMoney = stock.buyMoney
                valProfit = stock.valProfit
                profitNum = stock.profitNum
                updatedAt = stock.updatedAt
                team = teamEntity
                item = itemEntity
            } ?: stockMapper.toEntity(stock, teamEntity, itemEntity)

            stockRepository.save(entity)
        }
    }

    override fun deleteById(id: UUID) {
        stockRepository.deleteById(id)
    }
}
