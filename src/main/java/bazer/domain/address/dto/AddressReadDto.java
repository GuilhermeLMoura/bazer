package bazer.domain.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddressReadDto(
        @Schema(example = "1") Long id,
        @Schema(example = "01310100") String postalCode,
        @Schema(example = "500") String addressNumber,
        @Schema(example = "SP") String state,
        @Schema(example = "Bela Vista") String neighborhood,
        @Schema(example = "São Paulo") String city
) {
}