package bazer.domain.product.entity;

import bazer.domain.assessment_product.entity.AssessmentProduct;
import bazer.domain.category.entity.Category;
import bazer.domain.order.entity.ItemOrder;
import bazer.domain.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"assessments", "orderItems", "images"})
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private Integer stock;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer purchaseCount = 0;

    @ManyToOne
    @JoinColumn(name = "fk_category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "fk_store_id")
    private Profile store;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssessmentProduct> assessments;

    @OneToMany(mappedBy = "product")
    private List<ItemOrder> orderItems;
}
