package bazer.domain.assessment_product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AssessmentProductCreateDto(
        @NotNull @Min(1) @Max(5)
        @Schema(example = "5", description = "Nota de 1 a 5")
        Integer starQuantity,

        @Size(max = 500)
        @Schema(example = "Produto excelente, chegou rápido e bem embalado!")
        String comment,

        @NotNull
        @Schema(example = "1", description = "ID do produto avaliado")
        Long productId
) {
}