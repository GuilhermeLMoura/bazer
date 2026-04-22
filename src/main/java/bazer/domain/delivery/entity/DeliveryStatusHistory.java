package bazer.domain.delivery.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "delivery_status_history")
public class DeliveryStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45)
    private String description;

    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "fk_delivery_id")
    private Delivery delivery;
}
