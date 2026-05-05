package bazer.domain.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryReadDto(
        @Schema(example = "1") Long id,
        @Schema(example = "Eletrônicos") String name
) {
}