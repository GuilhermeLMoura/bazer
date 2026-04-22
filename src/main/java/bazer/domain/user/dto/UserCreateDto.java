package bazer.domain.user.dto;

import bazer.domain.user.entity.EnumRole;
import lombok.Data;

@Data
public class UserCreateDto {
    private String username;
    private String password;
    private EnumRole role;
}
