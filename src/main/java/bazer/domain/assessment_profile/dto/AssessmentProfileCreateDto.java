package bazer.domain.assessment_profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AssessmentProfileCreateDto(
        @NotNull @Min(1) @Max(5)
        @Schema(example = "4", description = "Nota de 1 a 5")
        Integer starQuantity,

        @Size(max = 500)
        @Schema(example = "Ótima loja, atendimento rápido e produtos de qualidade.")
        String comment,

        @NotNull
        @Schema(example = "1", description = "ID do perfil avaliado")
        Long profileId
) {
}