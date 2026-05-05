package bazer.domain.commission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CommissionCreateDto(
        @NotBlank @Size(max = 45)
        @Schema(example = "Comissão Padrão 10%")
        String name,

        @NotNull @DecimalMin("0.0001") @DecimalMax("0.9999")
        @Schema(example = "0.1000", description = "Taxa de comissão (0.1000 = 10%)")
        BigDecimal rate
) {
}