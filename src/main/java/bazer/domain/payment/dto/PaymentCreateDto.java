package bazer.domain.payment.dto;

import bazer.domain.payment.entity.EnumPaymentMethod;

import java.math.BigDecimal;

public record PaymentCreateDto(
        Long orderId,
        EnumPaymentMethod method,
        BigDecimal price,
        String txId,
        String pixCode,
        String qrCode
) {
}
