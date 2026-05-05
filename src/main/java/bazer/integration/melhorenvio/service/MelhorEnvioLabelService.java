package bazer.integration.melhorenvio.service;

import bazer.configuration.Exception.BusinessRuleException;
import bazer.domain.address.entity.Address;
import bazer.domain.order.entity.ItemOrder;
import bazer.domain.order.entity.Order;
import bazer.domain.profile.entity.Profile;
import bazer.integration.melhorenvio.config.MelhorEnvioProperties;
import bazer.integration.melhorenvio.dto.MelhorEnvioCartResponse;
import bazer.integration.melhorenvio.dto.MelhorEnvioGenerateItem;
import bazer.integration.melhorenvio.dto.ViaCepResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MelhorEnvioLabelService {

    private final MelhorEnvioAuthService authService;
    private final MelhorEnvioProperties props;

    public String processShipment(Order order) {
        if (props.isMockEnabled()) {
            return "MOCK-TRACK-" + order.getId();
        }

        ViaCepResponse destAddress = lookupCep(order.getPostalCodeDestination());

        String meOrderId = addToCart(order, destAddress);
        checkoutLabel(meOrderId);
        return generateLabel(meOrderId);
    }

    private String addToCart(Order order, ViaCepResponse destAddress) {
        Profile store = order.getStore();
        Profile buyer = order.getProfile();
        Address storeAddress = store.getAddresses().stream()
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("A loja não possui endereço cadastrado."));

        if (store.getPhone() == null || store.getPhone().isBlank()) {
            throw new BusinessRuleException("A loja \"" + store.getName() + "\" não possui telefone cadastrado. Cadastre o telefone no perfil da loja antes de gerar a etiqueta.");
        }
        if (buyer.getDocument() == null || buyer.getDocument().isBlank()) {
            throw new BusinessRuleException("O comprador não possui CPF cadastrado. O CPF é obrigatório para geração de etiqueta.");
        }

        Map<String, Object> from = buildAddress(
                store.getName(), store.getPhone(), store.getUser().getUsername(),
                store.getDocument(), storeAddress.getPostalCode(),
                storeAddress.getNeighborhood(), storeAddress.getAddressNumber(),
                storeAddress.getNeighborhood(), storeAddress.getCity(), storeAddress.getState()
        );

        Map<String, Object> to = buildAddress(
                buyer.getName(), buyer.getPhone(), buyer.getUser().getUsername(),
                buyer.getDocument(), order.getPostalCodeDestination(),
                destAddress.logradouro(), null,
                destAddress.bairro(), destAddress.localidade(), destAddress.uf()
        );

        List<Map<String, Object>> products = order.getItems().stream().map(item -> {
            Map<String, Object> p = new HashMap<>();
            p.put("name", item.getProduct().getName());
            p.put("quantity", item.getQuantity());
            p.put("unitary_value", item.getUnitPrice());
            return p;
        }).toList();

        Map<String, Object> volume = buildVolume(order.getItems());

        BigDecimal insuranceValue = order.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> options = Map.of(
                "insurance_value", insuranceValue,
                "receipt", false,
                "own_hand", false,
                "reverse", false,
                "non_commercial", true
        );

        Map<String, Object> body = new HashMap<>();
        body.put("service", order.getMeServiceId());
        body.put("from", from);
        body.put("to", to);
        body.put("products", products);
        body.put("volumes", List.of(volume));
        body.put("options", options);

        MelhorEnvioCartResponse response;
        try {
            response = RestClient.create()
                    .post()
                    .uri(props.getBaseUrl() + "/api/v2/me/cart")
                    .header("Authorization", "Bearer " + authService.getValidAccessToken())
                    .header("User-Agent", props.getUserAgent())
                    .header("Accept", "application/json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(MelhorEnvioCartResponse.class);
        } catch (RestClientResponseException e) {
            throw new BusinessRuleException("Melhor Envio recusou o pedido: " + e.getResponseBodyAsString());
        }

        if (response == null || response.id() == null) {
            throw new BusinessRuleException("Falha ao inserir pedido no carrinho do Melhor Envio.");
        }
        return response.id();
    }

    private void checkoutLabel(String meOrderId) {
        try {
            RestClient.create()
                    .post()
                    .uri(props.getBaseUrl() + "/api/v2/me/shipment/checkout")
                    .header("Authorization", "Bearer " + authService.getValidAccessToken())
                    .header("User-Agent", props.getUserAgent())
                    .header("Accept", "application/json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("orders", List.of(meOrderId)))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            int status = e.getStatusCode().value();
            if (status == 422 || status == 402) {
                throw new BusinessRuleException("Saldo insuficiente na carteira Melhor Envio. Recarregue o saldo e tente novamente.");
            }
            throw new BusinessRuleException("Erro ao confirmar etiqueta no Melhor Envio: HTTP " + status);
        }
    }

    private String generateLabel(String meOrderId) {
        Map<String, MelhorEnvioGenerateItem> response = RestClient.create()
                .post()
                .uri(props.getBaseUrl() + "/api/v2/me/shipment/generate")
                .header("Authorization", "Bearer " + authService.getValidAccessToken())
                .header("User-Agent", props.getUserAgent())
                .header("Accept", "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("orders", List.of(meOrderId)))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response == null || !response.containsKey(meOrderId)) {
            throw new BusinessRuleException("Falha ao gerar etiqueta no Melhor Envio.");
        }

        String tracking = response.get(meOrderId).tracking();
        if (tracking == null) {
            throw new BusinessRuleException("Melhor Envio não retornou código de rastreio.");
        }
        return tracking;
    }

    private ViaCepResponse lookupCep(String cep) {
        ViaCepResponse response = RestClient.create()
                .get()
                .uri("https://viacep.com.br/ws/" + cep + "/json/")
                .header("Accept", "application/json")
                .retrieve()
                .body(ViaCepResponse.class);

        if (response == null || response.uf() == null) {
            throw new BusinessRuleException("CEP de destino inválido ou não encontrado: " + cep);
        }
        return response;
    }

    private Map<String, Object> buildAddress(String name, String phone, String email,
                                              String document, String postalCode,
                                              String address, String number,
                                              String district, String city, String state) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone", phone != null ? phone : "");
        map.put("email", email);
        map.put("document", document);
        map.put("address", address != null ? address : "");
        map.put("number", number != null ? number : "S/N");
        map.put("district", district != null ? district : "");
        map.put("city", city);
        map.put("state_abbr", state);
        map.put("country_id", "BR");
        map.put("postal_code", postalCode);
        return map;
    }

    private Map<String, Object> buildVolume(List<ItemOrder> items) {
        double totalWeight = items.stream()
                .mapToDouble(i -> i.getProduct().getWeight().doubleValue() * i.getQuantity())
                .sum();
        int maxHeight = items.stream().mapToInt(i -> i.getProduct().getHeight()).max().orElse(1);
        int maxWidth = items.stream().mapToInt(i -> i.getProduct().getWidth()).max().orElse(1);
        int maxLength = items.stream().mapToInt(i -> i.getProduct().getLength()).max().orElse(1);

        return Map.of(
                "height", maxHeight,
                "width", maxWidth,
                "length", maxLength,
                "weight", totalWeight
        );
    }
}