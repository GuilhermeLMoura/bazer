package bazer.integration.melhorenvio.controller;

import bazer.integration.melhorenvio.service.MelhorEnvioAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MelhorEnvioController {

    private final MelhorEnvioAuthService authService;

    @GetMapping("/melhorenvio/authorize")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> authorize() {
        return ResponseEntity.status(302)
                .location(URI.create(authService.getAuthorizationUrl()))
                .build();
    }

    @GetMapping("/melhorenvio/auth-url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> authUrl() {
        return ResponseEntity.ok(Map.of("url", authService.getAuthorizationUrl()));
    }

    @GetMapping("/auth/callback")
    public ResponseEntity<String> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error) {
        if (error != null) {
            return ResponseEntity.badRequest().body("Autorização negada pelo Melhor Envio: " + error);
        }
        if (code == null) {
            return ResponseEntity.badRequest().body("Parâmetro 'code' ausente na resposta do Melhor Envio.");
        }
        authService.exchangeCode(code);
        return ResponseEntity.ok("Melhor Envio autorizado com sucesso!");
    }

    @PostMapping("/melhorenvio/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> refresh() {
        authService.refreshToken();
        return ResponseEntity.ok("Token renovado com sucesso!");
    }
}