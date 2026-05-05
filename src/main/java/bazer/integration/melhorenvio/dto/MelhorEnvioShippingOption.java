package bazer.integration.melhorenvio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MelhorEnvioShippingOption(
        Integer id,
        String name,
        String price,
        String error,
        @JsonProperty("delivery_time") Integer deliveryTime
) {
}