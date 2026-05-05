package bazer.domain.delivery.service;

import bazer.domain.delivery.dto.DeliveryReadDto;
import bazer.domain.delivery.entity.Delivery;
import bazer.domain.delivery.entity.EnumDeliveryStatus;
import bazer.domain.delivery.repository.DeliveryRepository;
import bazer.domain.order.entity.EnumOrderStatus;
import bazer.domain.order.entity.Order;
import bazer.domain.order.repository.OrderRepository;
import bazer.integration.melhorenvio.dto.MelhorEnvioWebhookDto;
import bazer.integration.melhorenvio.service.MelhorEnvioLabelService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final MelhorEnvioLabelService labelService;

    @Transactional
    public void createDelivery(Order order) {
        String trackingCode = labelService.processShipment(order);

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setPostalCode(order.getPostalCodeDestination());
        delivery.setStatus(EnumDeliveryStatus.IN_TRANSIT);
        delivery.setTrackingCode(trackingCode);
        delivery.setShippingDate(LocalDate.now());

        deliveryRepository.save(delivery);
    }

    @Transactional
    public void handleWebhook(MelhorEnvioWebhookDto dto) {
        if (dto.tracking() == null) return;

        deliveryRepository.findByTrackingCode(dto.tracking()).ifPresent(delivery -> {
            Order order = delivery.getOrder();
            switch (dto.status()) {
                case "delivered" -> {
                    delivery.setStatus(EnumDeliveryStatus.DELIVERED);
                    delivery.setDeliveredAt(LocalDate.now());
                    order.setStatus(EnumOrderStatus.DELIVERED);
                    orderRepository.save(order);
                }
                case "undelivered", "returned" -> delivery.setStatus(EnumDeliveryStatus.RETURNED);
                default -> { return; }
            }
            deliveryRepository.save(delivery);
        });
    }

    @Transactional(readOnly = true)
    public DeliveryReadDto findByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Entrega não encontrada para o pedido: " + orderId));
        return toDto(delivery);
    }

    private DeliveryReadDto toDto(Delivery d) {
        String trackingUrl = d.getTrackingCode() != null
                ? "https://melhorenvio.com.br/rastreamento/" + d.getTrackingCode()
                : null;
        return new DeliveryReadDto(
                d.getId(),
                d.getPostalCode(),
                d.getStatus(),
                d.getTrackingCode(),
                d.getCarrier(),
                d.getShippingDate(),
                d.getEstimatedDeliveryDate(),
                d.getDeliveredAt(),
                d.getOrder().getId(),
                trackingUrl
        );
    }
}