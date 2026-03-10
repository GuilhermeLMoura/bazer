package bazer.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserToken {
    private Long id;
    private String username;
    private String role;
    private String token;
}
