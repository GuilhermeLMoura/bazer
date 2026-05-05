package bazer.integration.melhorenvio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record MelhorEnvioWebhookDto(
        @Schema(example = "ME123456789BR", description = "Código de rastreio")
        String tracking,

        @Schema(example = "delivered", description = "Status: delivered | undelivered | returned")
        String status,

        @Schema(example = "Objeto entregue ao destinatário")
        String message,

        @JsonProperty("created_at")
        @Schema(example = "2026-05-01T14:00:00Z")
        String createdAt
) {
}