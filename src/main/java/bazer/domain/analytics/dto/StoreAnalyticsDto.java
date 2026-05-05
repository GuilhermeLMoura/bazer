package bazer.domain.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record StoreAnalyticsDto(
        @Schema(example = "42", description = "Total de pedidos com receita (excluindo PENDING e CANCELLED)") long totalOrders,
        @Schema(example = "8", description = "Pedidos com receita no mês atual") long monthOrders,
        @Schema(example = "15000.00", description = "Receita total acumulada") BigDecimal totalRevenue,
        @Schema(example = "1200.00", description = "Receita no mês atual") BigDecimal monthRevenue,
        @Schema(example = "357.14", description = "Ticket médio (totalRevenue / totalOrders)") BigDecimal averageTicket,
        @Schema(example = "15", description = "Compradores únicos que fizeram pedidos") long uniqueBuyers,
        @Schema(example = "1500.00", description = "Total de comissão paga à plataforma") BigDecimal totalCommissionPaid,
        @Schema(example = "120.00", description = "Comissão paga no mês atual") BigDecimal monthCommissionPaid,
        @Schema(description = "Contagem de pedidos por status") Map<String, Long> ordersByStatus,
        @Schema(description = "Top 5 produtos mais vendidos") List<TopProductDto> topProducts,
        @Schema(description = "Receita dos últimos 6 meses") List<MonthlyRevenueDto> last6MonthsRevenue
) {
}