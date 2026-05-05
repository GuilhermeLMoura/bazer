package bazer.integration.melhorenvio.dto;

import java.math.BigDecimal;

public record MelhorEnvioShippingResult(
        BigDecimal price,
        Integer serviceId,
        String serviceName,
        Integer estimatedDays
) {}