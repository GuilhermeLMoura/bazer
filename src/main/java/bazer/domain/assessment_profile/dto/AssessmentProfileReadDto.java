package bazer.domain.assessment_profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AssessmentProfileReadDto(
        @Schema(example = "1") Long id,
        @Schema(example = "4") Integer starQuantity,
        @Schema(example = "Ótima loja, atendimento rápido e produtos de qualidade.") String comment,
        @Schema(example = "2", description = "ID do perfil avaliado") Long profileId,
        @Schema(example = "3", description = "ID do perfil que fez a avaliação") Long reviewerId,
        @Schema(example = "João Silva") String reviewerName
) {
}