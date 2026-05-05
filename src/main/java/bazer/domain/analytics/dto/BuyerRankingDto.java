package bazer.domain.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record BuyerRankingDto(
        @Schema(example = "3") Long buyerId,
        @Schema(example = "João Silva") String buyerName,
        @Schema(example = "4500.00") BigDecimal totalSpent,
        @Schema(example = "12") long orderCount
) {
}