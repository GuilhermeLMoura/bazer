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

    @GetMapping("/cart")
    public ResponseEntity<OrderReadDto> getCart() {
        return ResponseEntity.ok(orderService.getOrCreateCart());
    }

    @PostMapping("/cart/items")
    public ResponseEntity<OrderReadDto> addItem(@RequestBody @Valid CartItemCreateDto dto) {
        return ResponseEntity.ok(orderService.addItem(dto));
    }

    @DeleteMapping("/cart/items/{itemId}")
    public ResponseEntity<OrderReadDto> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(orderService.removeItem(itemId));
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<OrderReadDto> checkout() {
        return ResponseEntity.ok(orderService.checkout());
    }

    // ─────────────── PEDIDOS - COMPRADOR ───────────────

    @GetMapping("/orders")
    public ResponseEntity<List<OrderReadDto>> listOrders() {
        return ResponseEntity.ok(orderService.listOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderReadDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    /** Confirma pagamento manualmente (AGUARDANDO_PAGAMENTO → CONFIRMED). */
    @PostMapping("/orders/{id}/confirm-payment")
    public ResponseEntity<OrderReadDto> confirmPayment(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.confirmPayment(id));
    }

    /** Cancela o pedido (comprador: só em AGUARDANDO_PAGAMENTO). */
    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<OrderReadDto> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    // ─────────────── PEDIDOS - VENDEDOR ───────────────

    /** Lista todos os pedidos que contêm produtos da loja do vendedor autenticado. */
    @GetMapping("/orders/store")
    public ResponseEntity<List<OrderReadDto>> listStoreOrders() {
        return ResponseEntity.ok(orderService.listStoreOrders());
    }

    /** Inicia processamento (CONFIRMED → PROCESSING). */
    @PostMapping("/orders/{id}/process")
    public ResponseEntity<OrderReadDto> processOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.processOrder(id));
    }

    /** Envia o pedido (PROCESSING → SHIPPED). */
    @PostMapping("/orders/{id}/ship")
    public ResponseEntity<OrderReadDto> shipOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.shipOrder(id));
    }

    /** Marca como entregue (SHIPPED → DELIVERED). */
    @PostMapping("/orders/{id}/deliver")
    public ResponseEntity<OrderReadDto> deliverOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.deliverOrder(id));
    }
}
