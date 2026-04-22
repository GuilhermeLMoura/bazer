package bazer.domain.assessment_profile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AssessmentProfileUpdateDto(
        @NotNull @Min(1) @Max(5) Integer starQuantity
) {
}
