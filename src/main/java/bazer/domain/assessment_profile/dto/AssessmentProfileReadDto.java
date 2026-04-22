package bazer.domain.assessment_profile.dto;

public record AssessmentProfileReadDto(
        Long id,
        Integer starQuantity,
        String comment,
        Long profileId,
        Long reviewerId,
        String reviewerName
) {
}
