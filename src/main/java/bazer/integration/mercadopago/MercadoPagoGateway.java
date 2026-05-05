package bazer.integration.mercadopago;

import bazer.configuration.Exception.BusinessRuleException;
import bazer.domain.order.entity.Order;
import bazer.domain.payment.gateway.GatewayChargeResult;
import bazer.domain.payment.gateway.PaymentGateway;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MercadoPagoGateway implements PaymentGateway {

    @Value("${mercadopago.notification-url}")
    private String notificationUrl;

    @Value("${mercadopago.mock-enabled:false}")
    private boolean mockEnabled;

    @Override
    public GatewayChargeResult createPixCharge(Order order, String payerEmail) {
        if (mockEnabled) {
            return new GatewayChargeResult(
                    "MOCK-" + order.getId(),
                    "00020126580014br.gov.bcb.pix0136mock-pix-key-bazer-order-" + order.getId() + "5204000053039865802BR5913Bazer Sandbox6008Sao Paulo62070503***6304MOCK",
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
                    "https://sandbox.mercadopago.com.br/sandbox/payments/mock-ticket"
            );
        }

        try {
            PaymentCreateRequest request = PaymentCreateRequest.builder()
                    .transactionAmount(order.getPrice())
                    .paymentMethodId("pix")
                    .description("Pedido Bazer #" + order.getId())
                    .externalReference(String.valueOf(order.getId()))
                    .notificationUrl(notificationUrl)
                    .payer(PaymentPayerRequest.builder()
                            .email(payerEmail)
                            .build())
                    .build();

            Payment payment = new PaymentClient().create(request);

            String pixCopyPaste = payment.getPointOfInteraction()
                    .getTransactionData().getQrCode();
            String qrBase64 = payment.getPointOfInteraction()
                    .getTransactionData().getQrCodeBase64();
            String ticketUrl = payment.getPointOfInteraction()
                    .getTransactionData().getTicketUrl();

            return new GatewayChargeResult(
                    String.valueOf(payment.getId()),
                    pixCopyPaste,
                    qrBase64,
                    ticketUrl
            );
        } catch (MPApiException e) {
            throw new BusinessRuleException("Erro ao criar cobrança PIX: " + e.getApiResponse().getContent());
        } catch (MPException e) {
            throw new BusinessRuleException("Erro de comunicação com Mercado Pago: " + e.getMessage());
        }
    }
}