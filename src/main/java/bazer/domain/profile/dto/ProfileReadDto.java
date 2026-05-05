package bazer.domain.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProfileReadDto(
        @Schema(example = "1") Long id,
        @Schema(example = "Loja Exemplo") String name,
        @Schema(example = "11111111000181") String document,
        @Schema(example = "http://localhost:8080/uploads/foto.jpg") String photo,
        @Schema(example = "11988880000") String phone,
        @Schema(example = "1", description = "ID da comissão atribuída (null se não tiver)") Long commissionId
) {
}