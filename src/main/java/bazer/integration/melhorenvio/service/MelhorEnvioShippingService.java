package bazer.integration.melhorenvio.service;

import bazer.domain.order.entity.ItemOrder;
import bazer.integration.melhorenvio.config.MelhorEnvioProperties;
import bazer.integration.melhorenvio.dto.MelhorEnvioShippingOption;
import bazer.integration.melhorenvio.dto.MelhorEnvioShippingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MelhorEnvioShippingService {

    private final MelhorEnvioAuthService authService;
    private final MelhorEnvioProperties props;

    public MelhorEnvioShippingResult calculateCheapest(String fromPostalCode, String toPostalCode, List<ItemOrder> items) {
        Map<String, Object> body = buildBody(fromPostalCode, toPostalCode, items);

        List<MelhorEnvioShippingOption> options = RestClient.create()
                .post()
                .uri(props.getBaseUrl() + "/api/v2/me/shipment/calculate")
                .header("Authorization", "Bearer " + authService.getValidAccessToken())
                .header("User-Agent", props.getUserAgent())
                .header("Accept", "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (options == null || options.isEmpty()) {
            throw new IllegalStateException("Nenhuma opção de frete disponível para este destino.");
        }

        return options.stream()
                .filter(o -> o.error() == null && o.price() != null)
                .min(Comparator.comparing(o -> new BigDecimal(o.price())))
                .map(o -> new MelhorEnvioShippingResult(
                        new BigDecimal(o.price()),
                        o.id(),
                        o.name(),
                        o.deliveryTime()
                ))
                .orElseThrow(() -> new IllegalStateException("Nenhuma opção de frete válida disponível para este destino."));
    }

    private Map<String, Object> buildBody(String from, String to, List<ItemOrder> items) {
        List<Map<String, Object>> products = items.stream().map(item -> {
            Map<String, Object> p = new HashMap<>();
            p.put("id", item.getProduct().getId().toString());
            p.put("width", item.getProduct().getWidth());
            p.put("height", item.getProduct().getHeight());
            p.put("length", item.getProduct().getLength());
            p.put("weight", item.getProduct().getWeight());
            p.put("insurance_value", item.getProduct().getPrice());
            p.put("quantity", item.getQuantity());
            return p;
        }).toList();

        return Map.of(
                "from", Map.of("postal_code", from),
                "to", Map.of("postal_code", to),
                "products", products
        );
    }
}