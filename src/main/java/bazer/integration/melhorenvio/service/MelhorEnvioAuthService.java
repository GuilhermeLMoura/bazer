package bazer.integration.melhorenvio.service;

import bazer.integration.melhorenvio.config.MelhorEnvioProperties;
import bazer.integration.melhorenvio.dto.MelhorEnvioTokenResponse;
import bazer.integration.melhorenvio.entity.MelhorEnvioToken;
import bazer.integration.melhorenvio.repository.MelhorEnvioTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MelhorEnvioAuthService {

    private final MelhorEnvioTokenRepository tokenRepository;
    private final MelhorEnvioProperties props;

    private static final String SCOPES =
            "cart-read cart-write shipping-calculate shipping-checkout " +
            "shipping-generate shipping-print shipping-tracking orders-read";

    public String getAuthorizationUrl() {
        String state = java.util.UUID.randomUUID().toString();
        return props.getBaseUrl() + "/oauth/authorize"
                + "?client_id=" + props.getClientId()
                + "&redirect_uri=" + props.getRedirectUri()
                + "&response_type=code"
                + "&scope=" + SCOPES.replace(" ", "%20")
                + "&state=" + state;
    }

    public MelhorEnvioToken exchangeCode(String code) {
        MelhorEnvioTokenResponse response = post(Map.of(
                "grant_type", "authorization_code",
                "client_id", props.getClientId(),
                "client_secret", props.getClientSecret(),
                "redirect_uri", props.getRedirectUri(),
                "code", code
        ));
        return saveToken(response);
    }

    public MelhorEnvioToken refreshToken() {
        MelhorEnvioToken current = tokenRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new IllegalStateException(
                        "Nenhum token encontrado. Autorize em /melhorenvio/authorize"));

        MelhorEnvioTokenResponse response = post(Map.of(
                "grant_type", "refresh_token",
                "client_id", props.getClientId(),
                "client_secret", props.getClientSecret(),
                "refresh_token", current.getRefreshToken()
        ));
        return saveToken(response);
    }

    public String getValidAccessToken() {
        MelhorEnvioToken token = tokenRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new IllegalStateException(
                        "Melhor Envio não autorizado. Acesse /melhorenvio/authorize"));

        if (LocalDateTime.now().isAfter(token.getExpiresAt().minusDays(1))) {
            token = refreshToken();
        }

        return token.getAccessToken();
    }

    private MelhorEnvioTokenResponse post(Map<String, String> body) {
        return RestClient.create()
                .post()
                .uri(props.getBaseUrl() + "/oauth/token")
                .header("User-Agent", props.getUserAgent())
                .header("Accept", "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(MelhorEnvioTokenResponse.class);
    }

    private MelhorEnvioToken saveToken(MelhorEnvioTokenResponse response) {
        MelhorEnvioToken token = new MelhorEnvioToken();
        token.setAccessToken(response.accessToken());
        token.setRefreshToken(response.refreshToken());
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusSeconds(response.expiresIn()));
        return tokenRepository.save(token);
    }
}
