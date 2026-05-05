package bazer.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserToken {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "loja@bazer.com")
    private String username;

    @Schema(example = "Loja Exemplo")
    private String name;

    @Schema(example = "ROLE_VENDEDOR")
    private String role;

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2phQGJhemVyLmNvbSJ9.abc123")
    private String token;
}