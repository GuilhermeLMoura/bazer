package bazer.domain.delivery.dto;

import bazer.domain.delivery.entity.EnumDeliveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record DeliveryReadDto(
        @Schema(example = "1") Long id,
        @Schema(example = "20040020") String postalCode,
        @Schema(example = "IN_TRANSIT", description = "IN_TRANSIT | DELIVERED | RETURNED") EnumDeliveryStatus status,
        @Schema(example = "ME123456789BR") String trackingCode,
        @Schema(example = "Correios") String carrier,
        @Schema(example = "2026-05-01") LocalDate shippingDate,
        @Schema(example = "2026-05-06") LocalDate estimatedDeliveryDate,
        @Schema(example = "2026-05-05", description = "Preenchido quando entregue") LocalDate deliveredAt,
        @Schema(example = "1") Long orderId,
        @Schema(example = "https://melhorenvio.com.br/rastreamento/ME123456789BR") String trackingUrl
) {
}