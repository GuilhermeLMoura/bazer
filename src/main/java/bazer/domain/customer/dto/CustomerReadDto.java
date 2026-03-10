package bazer.domain.customer.dto;

import lombok.Data;

@Data
public class CustomerReadDto {
    private Long id;
    private String nome;
    private String email;
    private String documento;
    private String cep;
    private String numeroEndereco;
}
