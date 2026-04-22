package bazer.domain.order.dto;

import bazer.domain.order.entity.EnumOrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreateDto(
        Long profileId,
        BigDecimal price,
        EnumOrderStatus status,
        List<Long> productIds
) {
}
