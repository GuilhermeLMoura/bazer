package bazer.domain.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record TopProductDto(
        @Schema(example = "Fone de Ouvido Bluetooth") String name,
        @Schema(example = "20", description = "Total de unidades vendidas") int totalQuantity,
        @Schema(example = "3000.00", description = "Receita total gerada pelo produto") BigDecimal totalRevenue
) {
}