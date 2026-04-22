package bazer.integration.melhorenvio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MelhorEnvioTokenResponse(
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") Long expiresIn,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken
) {}
