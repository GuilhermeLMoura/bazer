package bazer.domain.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record MonthlyRevenueDto(
        @Schema(example = "5", description = "Mês (1 = janeiro, 12 = dezembro)") int month,
        @Schema(example = "2026") int year,
        @Schema(example = "1200.00") BigDecimal revenue,
        @Schema(example = "4") long orderCount
) {
}