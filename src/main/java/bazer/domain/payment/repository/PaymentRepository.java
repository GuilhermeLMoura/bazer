package bazer.domain.payment.repository;

import bazer.domain.payment.entity.EnumPaymentStatus;
import bazer.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTxId(String txId);
    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> findByStatusAndExpiresAtBefore(EnumPaymentStatus status, LocalDateTime dateTime);
}
