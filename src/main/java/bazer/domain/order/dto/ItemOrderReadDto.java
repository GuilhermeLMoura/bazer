package bazer.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ItemOrderReadDto(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long productId,
        @Schema(example = "Fone de Ouvido Bluetooth") String productName,
        @Schema(example = "2") Integer quantity,
        @Schema(example = "150.00") BigDecimal unitPrice,
        @Schema(example = "300.00") BigDecimal subtotal
) {
}