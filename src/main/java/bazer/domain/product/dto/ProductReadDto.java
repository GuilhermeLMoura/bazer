package bazer.domain.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductReadDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Long categoryId,
        Long storeId,
        Integer purchaseCount,
        List<String> imageUrls
) {
}
