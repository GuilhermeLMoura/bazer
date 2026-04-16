package bazer.domain.order.dto;

import java.math.BigDecimal;

public record ItemOrderReadDto(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
