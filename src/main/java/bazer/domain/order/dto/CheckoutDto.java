package bazer.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CheckoutDto(
        @NotBlank @Size(min = 8, max = 8)
        @Schema(example = "20040020", description = "CEP de destino sem hífen (8 dígitos)")
        String postalCodeDestination
) {
}