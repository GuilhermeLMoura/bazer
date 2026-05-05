package bazer.domain.commission.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record CommissionReadDto(
        @Schema(example = "1") Long id,
        @Schema(example = "Comissão Padrão 10%") String name,
        @Schema(example = "0.1000", description = "Taxa de comissão (0.1000 = 10%)") BigDecimal rate
) {
}