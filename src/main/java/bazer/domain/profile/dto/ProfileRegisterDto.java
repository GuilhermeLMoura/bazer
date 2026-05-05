package bazer.domain.profile.dto;

import bazer.domain.user.entity.EnumRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRegisterDto {

    @NotBlank
    @Schema(example = "loja@bazer.com")
    private String username;

    @NotBlank
    @Schema(example = "Senha@123")
    private String password;

    @NotNull
    @Schema(example = "VENDEDOR")
    private EnumRole role;

    @NotBlank
    @Schema(example = "Loja Exemplo")
    private String name;

    @NotBlank
    @Size(max = 14)
    @Schema(example = "11111111000181")
    private String document;

    @Size(max = 15)
    @Schema(example = "11988880000")
    private String phone;

    @NotBlank
    @Size(max = 8)
    @Schema(example = "01310100")
    private String postalCode;

    @Size(max = 10)
    @Schema(example = "500")
    private String addressNumber;

    @Size(max = 45)
    @Schema(example = "SP")
    private String state;

    @Size(max = 45)
    @Schema(example = "Bela Vista")
    private String neighborhood;

    @Size(max = 45)
    @Schema(example = "São Paulo")
    private String city;

    @Schema(example = "1", description = "ID da comissão (opcional, atribuir via PATCH após criar)")
    private Long commissionId;
}