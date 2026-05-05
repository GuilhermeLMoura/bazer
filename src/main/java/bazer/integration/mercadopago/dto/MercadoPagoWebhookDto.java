package bazer.integration.mercadopago.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record MercadoPagoWebhookDto(
        @Schema(example = "12345678") Long id,
        @Schema(example = "payment") String type,
        @Schema(example = "payment.updated") String action,
        @JsonProperty("live_mode") @Schema(example = "false") Boolean liveMode,
        WebhookData data
) {
    public record WebhookData(
            @Schema(example = "987654321", description = "ID do pagamento no Mercado Pago")
            String id
    ) {}
}