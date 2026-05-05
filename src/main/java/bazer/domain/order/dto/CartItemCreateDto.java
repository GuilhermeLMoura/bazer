package bazer.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemCreateDto(
        @NotNull @Schema(example = "1", description = "ID do produto") Long productId,
        @NotNull @Min(1) @Schema(example = "2") Integer quantity
) {
}