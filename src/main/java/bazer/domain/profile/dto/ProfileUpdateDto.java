package bazer.domain.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateDto {

    @Size(max = 150)
    @Schema(example = "Loja Atualizada")
    private String name;

    @Size(max = 14)
    @Schema(example = "12345678909", description = "CPF (pessoa física) ou CNPJ (pessoa jurídica)")
    private String document;

    @Size(max = 15)
    @Schema(example = "11988880000")
    private String phone;
}