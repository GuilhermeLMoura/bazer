package bazer.domain.profile.dto;

import bazer.domain.user.entity.EnumRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRegisterDto {

    // User
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private EnumRole role;

    // Profile
    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 14)
    private String document;

    @Size(max = 15)
    private String phone;

    // Address
    @NotBlank
    @Size(max = 8)
    private String postalCode;

    @Size(max = 10)
    private String addressNumber;

    @Size(max = 45)
    private String state;

    @Size(max = 45)
    private String neighborhood;

    @Size(max = 45)
    private String city;
}
