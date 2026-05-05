package bazer.domain.payment.service;

import bazer.configuration.Exception.BusinessRuleException;
import bazer.domain.notification.NotificationService;
import bazer.domain.order.entity.EnumOrderStatus;
import bazer.domain.order.entity.Order;
import bazer.domain.order.repository.OrderRepository;
import bazer.domain.payment.dto.PaymentReadDto;
import bazer.domain.payment.entity.EnumPaymentMethod;
import bazer.domain.payment.entity.EnumPaymentStatus;
import bazer.domain.payment.entity.Payment;
import bazer.domain.payment.gateway.GatewayChargeResult;
import bazer.domain.payment.gateway.PaymentGateway;
import bazer.domain.payment.repository.PaymentRepository;
import bazer.domain.profile.entity.Profile;
import bazer.domain.profile.repository.ProfileRepository;
import bazer.integration.mercadopago.dto.MercadoPagoWebhookDto;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProfileRepository profileRepository;
    private final PaymentGateway paymentGateway;
    private final NotificationService notificationService;

    private static final int PIX_EXPIRATION_MINUTES = 30;

    @Transactional
    public PaymentReadDto initiatePixPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + orderId));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile profile = profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        if (!order.getProfile().getId().equals(profile.getId())) {
            throw new BusinessRuleException("Este pedido não pertence ao seu perfil.");
        }

        if (order.getStatus() != EnumOrderStatus.AGUARDANDO_PAGAMENTO
                && order.getStatus() != EnumOrderStatus.PAGAMENTO_EXPIRADO) {
            throw new BusinessRuleException("Pedido não está aguardando pagamento. Status atual: " + order.getStatus());
        }

        paymentRepository.findByOrderId(orderId).ifPresent(p -> {
            if (p.getStatus() == EnumPaymentStatus.PENDING) {
                throw new BusinessRuleException("Já existe uma cobrança PIX pendente para este pedido.");
            }
        });

        if (order.getStatus() == EnumOrderStatus.PAGAMENTO_EXPIRADO) {
            order.setStatus(EnumOrderStatus.AGUARDANDO_PAGAMENTO);
            orderRepository.save(order);
        }

        GatewayChargeResult result = paymentGateway.createPixCharge(order, profile.getUser().getUsername());

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(EnumPaymentMethod.PIX);
        payment.setStatus(EnumPaymentStatus.PENDING);
        payment.setPrice(order.getPrice());
        payment.setTxId(result.externalId());
        payment.setPixCode(result.pixCopyPaste());
        payment.setQrCode(result.qrCodeBase64());
        payment.setTicketUrl(result.ticketUrl());
        payment.setCreatedDate(LocalDateTime.now());
        payment.setExpiresAt(LocalDateTime.now().plusMinutes(PIX_EXPIRATION_MINUTES));

        return toDto(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public PaymentReadDto getPixStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + orderId));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile profile = profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        if (!order.getProfile().getId().equals(profile.getId())) {
            throw new BusinessRuleException("Este pedido não pertence ao seu perfil.");
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum pagamento encontrado para o pedido: " + orderId));

        return toDto(payment);
    }

    @Transactional
    public void handleWebhook(MercadoPagoWebhookDto webhook) {
        if (!"payment".equals(webhook.type()) || webhook.data() == null) {
            return;
        }

        try {
            com.mercadopago.resources.payment.Payment mpPayment =
                    new PaymentClient().get(Long.parseLong(webhook.data().id()));

            String status = mpPayment.getStatus();

            paymentRepository.findByTxId(String.valueOf(mpPayment.getId())).ifPresent(payment -> {
                if ("approved".equals(status)) {
                    payment.setStatus(EnumPaymentStatus.APPROVED);
                    payment.setPaymentDate(LocalDateTime.now());
                    paymentRepository.save(payment);

                    Order order = payment.getOrder();
                    order.setStatus(EnumOrderStatus.CONFIRMED);
                    orderRepository.save(order);

                    notificationService.sendOrderConfirmedToAdmins(order);

                } else if ("rejected".equals(status) || "cancelled".equals(status)) {
                    payment.setStatus(EnumPaymentStatus.REJECTED);
                    paymentRepository.save(payment);
                }
            });

        } catch (MPApiException | MPException e) {
            System.err.println("Erro ao processar webhook MP: " + e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void expirePixPayments() {
        List<Payment> expired = paymentRepository
                .findByStatusAndExpiresAtBefore(EnumPaymentStatus.PENDING, LocalDateTime.now());

        for (Payment payment : expired) {
            payment.setStatus(EnumPaymentStatus.REJECTED);
            paymentRepository.save(payment);

            Order order = payment.getOrder();
            if (order.getStatus() == EnumOrderStatus.AGUARDANDO_PAGAMENTO) {
                order.setStatus(EnumOrderStatus.PAGAMENTO_EXPIRADO);
                orderRepository.save(order);
            }
        }
    }

    private PaymentReadDto toDto(Payment p) {
        return new PaymentReadDto(
                p.getId(),
                p.getOrder().getId(),
                p.getMethod(),
                p.getStatus(),
                p.getPrice(),
                p.getTxId(),
                p.getPixCode(),
                p.getQrCode(),
                p.getCreatedDate(),
                p.getPaymentDate(),
                p.getTicketUrl(),
                p.getExpiresAt()
        );
    }
}