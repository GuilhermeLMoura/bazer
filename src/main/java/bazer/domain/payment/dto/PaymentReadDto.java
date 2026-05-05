package bazer.domain.payment.dto;

import bazer.domain.payment.entity.EnumPaymentMethod;
import bazer.domain.payment.entity.EnumPaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentReadDto(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long orderId,
        @Schema(example = "PIX") EnumPaymentMethod method,
        @Schema(example = "PENDING", description = "PENDING | APPROVED | REJECTED") EnumPaymentStatus status,
        @Schema(example = "337.50") BigDecimal price,
        @Schema(example = "987654321", description = "ID da transação no Mercado Pago") String txId,
        @Schema(example = "00020126580014br.gov.bcb.pix...", description = "Código PIX copia e cola") String pixCode,
        @Schema(example = "iVBORw0KGgo...", description = "QR Code em base64") String qrCode,
        @Schema(example = "2026-05-01T10:31:00") LocalDateTime createdDate,
        @Schema(example = "2026-05-01T10:35:00", description = "Preenchido após aprovação") LocalDateTime paymentDate,
        @Schema(example = "https://www.mercadopago.com.br/checkout/...", description = "Link para pagar no MP") String ticketUrl,
        @Schema(example = "2026-05-01T11:01:00", description = "PIX expira 30 minutos após a geração") LocalDateTime expiresAt
) {
}