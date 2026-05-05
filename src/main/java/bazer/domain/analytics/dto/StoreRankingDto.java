package bazer.domain.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record StoreRankingDto(
        @Schema(example = "2") Long storeId,
        @Schema(example = "Loja Exemplo") String storeName,
        @Schema(example = "15000.00") BigDecimal revenue,
        @Schema(example = "42") long orderCount
) {
}