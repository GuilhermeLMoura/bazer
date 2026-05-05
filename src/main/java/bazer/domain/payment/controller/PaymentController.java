package bazer.domain.payment.controller;

import bazer.domain.payment.dto.PaymentReadDto;
import bazer.domain.payment.service.PaymentService;
import bazer.integration.mercadopago.dto.MercadoPagoWebhookDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pix/{orderId}")
    public ResponseEntity<PaymentReadDto> initiatePixPayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.initiatePixPayment(orderId));
    }

    @GetMapping("/pix/{orderId}")
    public ResponseEntity<PaymentReadDto> getPixStatus(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPixStatus(orderId));
    }

    @PostMapping("/webhook/mercadopago")
    public ResponseEntity<Void> mercadoPagoWebhook(@RequestBody MercadoPagoWebhookDto webhook) {
        paymentService.handleWebhook(webhook);
        return ResponseEntity.ok().build();
    }
}