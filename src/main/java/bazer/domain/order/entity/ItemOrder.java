package bazer.domain.order.entity;

import bazer.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "item_order")
public class ItemOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "fk_order_id")
    private Order order;

    private Integer quantity;

    /** Preço unitário no momento da adição ao carrinho */
    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice;
}
