package team.mozu.dsm.adapter.out.team.persistence.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import team.mozu.dsm.adapter.out.team.entity.StockJpaEntity
import java.util.UUID

interface StockRepository : CrudRepository<StockJpaEntity, UUID> {

    /**
     * teamId, itemId 필드가 직접적으로 없어서 쿼리 구현
     */
    @Query("SELECT s FROM StockJpaEntity s WHERE s.team.id = :teamId AND s.item.id = :itemId")
    fun findByTeamIdAndItemId(@Param("teamId") teamId: UUID, @Param("itemId") itemId: UUID): StockJpaEntity?

    @Query("SELECT s FROM StockJpaEntity s WHERE s.team.id = :teamId")
    fun findAllByTeamId(@Param("teamId") teamId: UUID): List<StockJpaEntity>
}
