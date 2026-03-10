package bazer.domain.customer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cliente")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 150)
    private String nome;
    @Column(length = 14)
    private String documento; //cpf or cnpj
    @Column(length = 8)
    private String cep;
    @Column(length = 50)
    private String numeroEndereco; //example : 1000-A or 1000
}
