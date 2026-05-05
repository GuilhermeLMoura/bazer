package bazer.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserLogin {

    @Schema(example = "admin@bazer.com")
    private String username;

    @Schema(example = "Admin@123")
    private String password;
}