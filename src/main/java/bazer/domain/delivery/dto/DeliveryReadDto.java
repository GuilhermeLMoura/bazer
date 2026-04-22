package bazer.domain.delivery.dto;

import bazer.domain.delivery.entity.EnumDeliveryStatus;

import java.time.LocalDate;

public record DeliveryReadDto(
        Long id,
        String postalCode,
        EnumDeliveryStatus status,
        String trackingCode,
        String carrier,
        LocalDate shippingDate,
        LocalDate estimatedDeliveryDate,
        LocalDate deliveredAt,
        Long orderId
) {
}
