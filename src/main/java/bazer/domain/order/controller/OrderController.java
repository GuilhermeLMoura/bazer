package bazer.domain.order.controller;

import bazer.domain.order.dto.CartItemCreateDto;
import bazer.domain.order.dto.OrderReadDto;
import bazer.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ─────────────── CARRINHO (Order PENDING) ───────────────

    /** Visualiza o carrinho ativo ou cria um novo se não existir */
    @GetMapping("/cart")
    public ResponseEntity<OrderReadDto> getCart() {
        return ResponseEntity.ok(orderService.getOrCreateCart());
    }

    /** Adiciona um produto ao carrinho */
    @PostMapping("/cart/items")
    public ResponseEntity<OrderReadDto> addItem(@RequestBody @Valid CartItemCreateDto dto) {
        return ResponseEntity.ok(orderService.addItem(dto));
    }

    /** Remove um item do carrinho */
    @DeleteMapping("/cart/items/{itemId}")
    public ResponseEntity<OrderReadDto> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(orderService.removeItem(itemId));
    }

    /** Finaliza o carrinho: PENDING → AGUARDANDO_PAGAMENTO (vira pedido real) */
    @PostMapping("/cart/checkout")
    public ResponseEntity<OrderReadDto> checkout() {
        return ResponseEntity.ok(orderService.checkout());
    }

    // ─────────────── PEDIDOS (Order != PENDING) ───────────────

    /** Lista todos os pedidos reais do usuário logado */
    @GetMapping("/orders")
    public ResponseEntity<List<OrderReadDto>> listOrders() {
        return ResponseEntity.ok(orderService.listOrders());
    }

    /** Busca um pedido pelo ID */
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderReadDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }
}
