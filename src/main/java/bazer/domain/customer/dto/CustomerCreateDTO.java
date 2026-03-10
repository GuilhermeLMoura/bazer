package bazer.domain.customer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerCreateDTO {
    @NotBlank(message = "O cliente deve conter nome")
    private String nome;
    private String documento;
    private String cep;
    private String numeroEndereco;
}
