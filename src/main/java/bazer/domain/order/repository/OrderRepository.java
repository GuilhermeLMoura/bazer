package bazer.domain.order.repository;

import bazer.domain.order.entity.EnumOrderStatus;
import bazer.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /** Busca o carrinho ativo (PENDING) de um perfil */
    Optional<Order> findByProfileIdAndStatus(Long profileId, EnumOrderStatus status);

    /** Lista pedidos reais (excluindo o carrinho PENDING) de um perfil */
    List<Order> findByProfileIdAndStatusNot(Long profileId, EnumOrderStatus status);

    /** Lista pedidos que contêm produtos de uma loja, excluindo PENDING */
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.store.id = :storeId AND o.status <> :status")
    List<Order> findByStoreIdAndStatusNot(@Param("storeId") Long storeId, @Param("status") EnumOrderStatus status);
}
