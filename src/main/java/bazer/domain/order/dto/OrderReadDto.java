package bazer.domain.order.dto;

import bazer.domain.order.entity.EnumOrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderReadDto(
        Long id,
        Long profileId,
        EnumOrderStatus status,
        BigDecimal total,
        List<ItemOrderReadDto> items
) {
}
