package bazer.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Item a ser adicionado no pedido (status PENDING = carrinho) */
public record CartItemCreateDto(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity
) {
}
