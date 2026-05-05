package bazer.domain.payment.gateway;

public record GatewayChargeResult(
        String externalId,
        String pixCopyPaste,
        String qrCodeBase64,
        String ticketUrl
) {}