package bazer.domain.assessment_product.dto;

public record AssessmentProductReadDto(
        Long id,
        Integer starQuantity,
        String comment,
        Long productId,
        Long reviewerId,
        String reviewerName
) {
}
