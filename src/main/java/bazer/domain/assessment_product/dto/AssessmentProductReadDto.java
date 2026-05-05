package bazer.domain.assessment_product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AssessmentProductReadDto(
        @Schema(example = "1") Long id,
        @Schema(example = "5") Integer starQuantity,
        @Schema(example = "Produto excelente, chegou rápido e bem embalado!") String comment,
        @Schema(example = "1") Long productId,
        @Schema(example = "3", description = "ID do perfil que fez a avaliação") Long reviewerId,
        @Schema(example = "João Silva") String reviewerName
) {
}