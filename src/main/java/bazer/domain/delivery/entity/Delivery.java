package bazer.domain.delivery.entity;

import bazer.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = "statusHistory")
@Table(name = "delivery")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private EnumDeliveryStatus status;

    @Column(length = 45)
    private String trackingCode;

    @Column(length = 45)
    private String carrier;

    private LocalDate shippingDate;

    private LocalDate estimatedDeliveryDate;

    private LocalDate deliveredAt;

    @OneToOne
    @JoinColumn(name = "fk_order_id")
    private Order order;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryStatusHistory> statusHistory;
}
