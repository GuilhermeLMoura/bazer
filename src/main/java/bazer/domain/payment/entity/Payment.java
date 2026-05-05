package bazer.domain.payment.entity;

import bazer.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "fk_order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private EnumPaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private EnumPaymentStatus status;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 100)
    private String txId;

    @Column(columnDefinition = "TEXT")
    private String pixCode;

    @Column(columnDefinition = "TEXT")
    private String qrCode;

    @Column(columnDefinition = "TEXT")
    private String ticketUrl;

    private LocalDateTime createdDate;

    private LocalDateTime paymentDate;

    private LocalDateTime expiresAt;
}
