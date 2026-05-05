package bazer.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductCreateDto(
        @NotBlank @Size(max = 45)
        @Schema(example = "Fone de Ouvido Bluetooth")
        String name,

        @Size(max = 255)
        @Schema(example = "Fone sem fio com cancelamento de ruído, bateria 30h")
        String description,

        @NotNull @DecimalMin("0.01")
        @Schema(example = "150.00")
        BigDecimal price,

        @NotNull @Min(0)
        @Schema(example = "50")
        Integer stock,

        @NotNull
        @Schema(example = "1", description = "ID da categoria")
        Long categoryId,

        @DecimalMin("0.001")
        @Schema(example = "0.300", description = "Peso em kg (obrigatório para cálculo de frete)")
        BigDecimal weight,

        @Min(1)
        @Schema(example = "20", description = "Largura em cm")
        Integer width,

        @Min(1)
        @Schema(example = "10", description = "Altura em cm")
        Integer height,

        @Min(1)
        @Schema(example = "15", description = "Comprimento em cm")
        Integer length
) {
}