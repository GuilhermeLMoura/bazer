package bazer.domain.payment.dto;

import bazer.domain.payment.entity.EnumPaymentMethod;
import bazer.domain.payment.entity.EnumPaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentReadDto(
        Long id,
        Long orderId,
        EnumPaymentMethod method,
        EnumPaymentStatus status,
        BigDecimal price,
        String txId,
        String pixCode,
        String qrCode,
        LocalDateTime createdDate,
        LocalDateTime paymentDate
) {
}
