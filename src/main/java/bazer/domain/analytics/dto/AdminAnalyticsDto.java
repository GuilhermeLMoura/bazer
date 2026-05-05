package bazer.domain.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record AdminAnalyticsDto(
        @Schema(example = "120", description = "Total de pedidos na plataforma (excluindo PENDING)") long totalOrders,
        @Schema(example = "85", description = "Pedidos entregues") long deliveredOrders,
        @Schema(example = "45000.00", description = "Receita total da plataforma") BigDecimal platformRevenue,
        @Schema(example = "4500.00", description = "Comissão arrecadada no mês atual") BigDecimal monthCommission,
        @Schema(example = "32000.00", description = "Comissão arrecadada no ano filtrado") BigDecimal yearlyCommission,
        @Schema(example = "2026", description = "Ano de referência para yearlyCommission") int commissionYear,
        @Schema(description = "Contagem de pedidos por status") Map<String, Long> ordersByStatus,
        @Schema(description = "Top 10 lojas por receita") List<StoreRankingDto> topStores,
        @Schema(description = "Top 10 compradores por gasto") List<BuyerRankingDto> topBuyers
) {
}