package bazer.integration.melhorenvio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MelhorEnvioGenerateItem(
        Long id,
        String protocol,
        String tracking,
        @JsonProperty("service_id") Integer serviceId
) {}