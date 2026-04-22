package bazer.domain.assessment_product.entity;

import bazer.domain.product.entity.Product;
import bazer.domain.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "assessment_product")
public class AssessmentProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer starQuantity;

    @Column(length = 500)
    private String comment;

    /** Produto que está sendo avaliado */
    @ManyToOne
    @JoinColumn(name = "fk_product_id")
    private Product product;

    /** Perfil COMPRADOR que realizou a avaliação */
    @ManyToOne
    @JoinColumn(name = "fk_reviewer_id")
    private Profile reviewer;
}
