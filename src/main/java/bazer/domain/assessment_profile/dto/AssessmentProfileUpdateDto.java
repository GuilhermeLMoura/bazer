package bazer.domain.assessment_profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AssessmentProfileUpdateDto(
        @NotNull @Min(1) @Max(5)
        @Schema(example = "4", description = "Nova nota de 1 a 5")
        Integer starQuantity
) {
}