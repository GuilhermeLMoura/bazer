package bazer.domain.assessment_product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AssessmentProductCreateDto(
        @NotNull @Min(1) @Max(5) Integer starQuantity,
        @Size(max = 500) String comment,
        @NotNull Long productId
) {
}
