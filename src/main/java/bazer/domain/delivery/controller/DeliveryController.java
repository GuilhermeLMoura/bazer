package bazer.domain.delivery.controller;

import bazer.domain.delivery.dto.DeliveryReadDto;
import bazer.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryReadDto> findByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.findByOrderId(orderId));
    }
}