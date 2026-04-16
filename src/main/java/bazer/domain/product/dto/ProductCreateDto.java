package bazer.domain.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductCreateDto(
        @NotBlank @Size(max = 45) String name,
        @Size(max = 255) String description,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotNull @Min(0) Integer stock,
        @NotNull Long categoryId
) {
}
