package team.mozu.dsm.adapter.`in`.team.dto.response

data class TradingDetailResponse(
    val itemId: Int,
    val itemName: String,
    val holdingQuantity: Int, // 보유 주식수
    val purchasePrice: Long, // 매입가 (총 매입금액)
    val currentPrice: Long, // 현재가
    val valuationAmount: Long, // 평가금액 (보유 주식수 × 현재가)
    val profitLoss: Long, // 평가손익
    val profitLossRate: Double // 수익률 (%)
)
