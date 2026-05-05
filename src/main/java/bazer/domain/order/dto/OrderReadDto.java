package bazer.domain.order.dto;

import bazer.domain.order.entity.EnumOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderReadDto(
        @Schema(example = "1") Long id,
        @Schema(example = "3", description = "ID do comprador") Long profileId,
        @Schema(example = "2", description = "ID da loja") Long storeId,
        @Schema(example = "CONFIRMED", description = "PENDING | AGUARDANDO_PAGAMENTO | CONFIRMED | PROCESSING | SHIPPED | DELIVERED | CANCELLED") EnumOrderStatus status,
        @Schema(example = "337.50", description = "Total do pedido (produtos + frete + comissão)") BigDecimal total,
        @Schema(example = "37.50") BigDecimal shippingCost,
        @Schema(example = "30.00") BigDecimal commissionAmount,
        @Schema(description = "Itens do pedido") List<ItemOrderReadDto> items,
        @Schema(example = "2026-05-01T10:30:00") LocalDateTime createdAt
) {
}