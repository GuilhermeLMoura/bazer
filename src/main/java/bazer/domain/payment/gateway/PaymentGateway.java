package bazer.domain.payment.gateway;

import bazer.domain.order.entity.Order;

public interface PaymentGateway {
    GatewayChargeResult createPixCharge(Order order, String payerEmail);
}