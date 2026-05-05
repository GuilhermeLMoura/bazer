package bazer.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public record ProductReadDto(
        @Schema(example = "1") Long id,
        @Schema(example = "Fone de Ouvido Bluetooth") String name,
        @Schema(example = "Fone sem fio com cancelamento de ruído, bateria 30h") String description,
        @Schema(example = "150.00") BigDecimal price,
        @Schema(example = "50") Integer stock,
        @Schema(example = "1") Long categoryId,
        @Schema(example = "2", description = "ID da loja (perfil VENDEDOR)") Long storeId,
        @Schema(example = "10", description = "Total de vezes que o produto foi comprado") Integer purchaseCount,
        @Schema(description = "URLs das imagens do produto") List<String> imageUrls,
        @Schema(example = "0.300", description = "Peso em kg") BigDecimal weight,
        @Schema(example = "20", description = "Largura em cm") Integer width,
        @Schema(example = "10", description = "Altura em cm") Integer height,
        @Schema(example = "15", description = "Comprimento em cm") Integer length
) {
}