package bazer.domain.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddressCreateDto(
        @Schema(example = "01310100", description = "CEP sem hífen (8 dígitos)") String postalCode,
        @Schema(example = "500") String addressNumber,
        @Schema(example = "SP") String state,
        @Schema(example = "Bela Vista") String neighborhood,
        @Schema(example = "São Paulo") String city
) {
}