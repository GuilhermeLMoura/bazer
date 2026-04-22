package bazer.domain.order.service;

import bazer.configuration.Exception.BusinessRuleException;
import bazer.domain.order.dto.CartItemCreateDto;
import bazer.domain.order.dto.ItemOrderReadDto;
import bazer.domain.order.dto.OrderReadDto;
import bazer.domain.order.entity.EnumOrderStatus;
import bazer.domain.order.entity.ItemOrder;
import bazer.domain.order.entity.Order;
import bazer.domain.order.repository.ItemOrderRepository;
import bazer.domain.order.repository.OrderRepository;
import bazer.domain.product.entity.Product;
import bazer.domain.product.repository.ProductRepository;
import bazer.domain.profile.entity.Profile;
import bazer.domain.profile.repository.ProfileRepository;
import bazer.domain.user.security.UserCustomDetail;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemOrderRepository itemOrderRepository;
    private final ProductRepository productRepository;
    private final ProfileRepository profileRepository;

    // ─────────────────────────────────────────────────────────
    // CARRINHO
    // ─────────────────────────────────────────────────────────

    @Transactional
    public OrderReadDto getOrCreateCart() {
        Profile profile = getAuthenticatedProfile();
        Order cart = orderRepository.findByProfileIdAndStatus(profile.getId(), EnumOrderStatus.PENDING)
                .orElseGet(() -> {
                    Order newCart = new Order();
                    newCart.setProfile(profile);
                    newCart.setStatus(EnumOrderStatus.PENDING);
                    newCart.setPrice(BigDecimal.ZERO);
                    return orderRepository.save(newCart);
                });
        return toDto(cart);
    }

    @Transactional
    public OrderReadDto addItem(CartItemCreateDto dto) {
        Profile profile = getAuthenticatedProfile();
        Order cart = getActiveCart(profile.getId());

        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + dto.productId()));

        if (product.getStock() < dto.quantity()) {
            throw new BusinessRuleException("Estoque insuficiente. Disponível: " + product.getStock());
        }

        ItemOrder item = cart.getItems() == null ? null :
                cart.getItems().stream()
                        .filter(i -> i.getProduct().getId().equals(dto.productId()))
                        .findFirst().orElse(null);

        if (item != null) {
            item.setQuantity(item.getQuantity() + dto.quantity());
            itemOrderRepository.save(item);
        } else {
            item = new ItemOrder();
            item.setProduct(product);
            item.setOrder(cart);
            item.setQuantity(dto.quantity());
            item.setUnitPrice(product.getPrice());
            itemOrderRepository.save(item);
        }

        recalculateTotal(cart);
        return toDto(orderRepository.save(cart));
    }

    @Transactional
    public OrderReadDto removeItem(Long itemId) {
        Profile profile = getAuthenticatedProfile();
        Order cart = getActiveCart(profile.getId());

        ItemOrder item = itemOrderRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado: " + itemId));

        if (!item.getOrder().getId().equals(cart.getId())) {
            throw new BusinessRuleException("Este item não pertence ao seu carrinho.");
        }

        itemOrderRepository.delete(item);
        cart.getItems().remove(item);
        recalculateTotal(cart);
        return toDto(orderRepository.save(cart));
    }

    @Transactional
    public OrderReadDto checkout() {
        Profile profile = getAuthenticatedProfile();
        Order cart = getActiveCart(profile.getId());

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessRuleException("Não é possível finalizar um carrinho vazio.");
        }

        cart.setStatus(EnumOrderStatus.AGUARDANDO_PAGAMENTO);
        return toDto(orderRepository.save(cart));
    }

    // ─────────────────────────────────────────────────────────
    // LISTAGENS - COMPRADOR
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<OrderReadDto> listOrders() {
        Profile profile = getAuthenticatedProfile();
        return orderRepository.findByProfileIdAndStatusNot(profile.getId(), EnumOrderStatus.PENDING)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public OrderReadDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + id));
        return toDto(order);
    }

    // ─────────────────────────────────────────────────────────
    // LISTAGENS - VENDEDOR
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('VENDEDOR')")
    public List<OrderReadDto> listStoreOrders() {
        Profile store = getAuthenticatedProfile();
        return orderRepository.findByStoreIdAndStatusNot(store.getId(), EnumOrderStatus.PENDING)
                .stream().map(this::toDto).toList();
    }

    // ─────────────────────────────────────────────────────────
    // TRANSIÇÕES DE STATUS
    // ─────────────────────────────────────────────────────────

    /**
     * Simula confirmação de pagamento (AGUARDANDO_PAGAMENTO → CONFIRMED).
     * Será substituído pelo PaymentService quando integrado.
     */
    @Transactional
    public OrderReadDto confirmPayment(Long orderId) {
        Order order = getOrThrow(orderId);
        validateOwnership(order);
        validateTransition(order, EnumOrderStatus.AGUARDANDO_PAGAMENTO, EnumOrderStatus.CONFIRMED);
        order.setStatus(EnumOrderStatus.CONFIRMED);
        return toDto(orderRepository.save(order));
    }

    /**
     * Vendedor inicia separação/processamento (CONFIRMED → PROCESSING).
     */
    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public OrderReadDto processOrder(Long orderId) {
        Order order = getOrThrow(orderId);
        validateStoreOwnership(order);
        validateTransition(order, EnumOrderStatus.CONFIRMED, EnumOrderStatus.PROCESSING);
        order.setStatus(EnumOrderStatus.PROCESSING);
        return toDto(orderRepository.save(order));
    }

    /**
     * Vendedor envia o pedido (PROCESSING → SHIPPED).
     */
    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public OrderReadDto shipOrder(Long orderId) {
        Order order = getOrThrow(orderId);
        validateStoreOwnership(order);
        validateTransition(order, EnumOrderStatus.PROCESSING, EnumOrderStatus.SHIPPED);
        order.setStatus(EnumOrderStatus.SHIPPED);
        return toDto(orderRepository.save(order));
    }

    /**
     * Vendedor marca como entregue (SHIPPED → DELIVERED).
     */
    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public OrderReadDto deliverOrder(Long orderId) {
        Order order = getOrThrow(orderId);
        validateStoreOwnership(order);
        validateTransition(order, EnumOrderStatus.SHIPPED, EnumOrderStatus.DELIVERED);
        order.setStatus(EnumOrderStatus.DELIVERED);
        return toDto(orderRepository.save(order));
    }

    /**
     * Cancela o pedido.
     * - Comprador: só pode cancelar em AGUARDANDO_PAGAMENTO.
     * - Vendedor: pode cancelar em AGUARDANDO_PAGAMENTO, CONFIRMED ou PROCESSING.
     */
    @Transactional
    public OrderReadDto cancelOrder(Long orderId) {
        Order order = getOrThrow(orderId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isVendedor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VENDEDOR"));

        List<EnumOrderStatus> allowedForVendedor = List.of(
                EnumOrderStatus.AGUARDANDO_PAGAMENTO,
                EnumOrderStatus.CONFIRMED,
                EnumOrderStatus.PROCESSING
        );
        List<EnumOrderStatus> allowedForComprador = List.of(
                EnumOrderStatus.AGUARDANDO_PAGAMENTO
        );

        List<EnumOrderStatus> allowed = isVendedor ? allowedForVendedor : allowedForComprador;

        if (!allowed.contains(order.getStatus())) {
            throw new BusinessRuleException(
                    "Não é possível cancelar um pedido com status: " + order.getStatus()
            );
        }

        if (isVendedor) {
            validateStoreOwnership(order);
        } else {
            validateOwnership(order);
        }

        order.setStatus(EnumOrderStatus.CANCELLED);
        return toDto(orderRepository.save(order));
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────

    private Order getOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + id));
    }

    private Order getActiveCart(Long profileId) {
        return orderRepository.findByProfileIdAndStatus(profileId, EnumOrderStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum carrinho ativo encontrado."));
    }

    private void recalculateTotal(Order cart) {
        if (cart.getItems() == null) {
            cart.setPrice(BigDecimal.ZERO);
            return;
        }
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setPrice(total);
    }

    /** Valida que o pedido pertence ao comprador autenticado. */
    private void validateOwnership(Order order) {
        Profile profile = getAuthenticatedProfile();
        if (!order.getProfile().getId().equals(profile.getId())) {
            throw new BusinessRuleException("Este pedido não pertence ao seu perfil.");
        }
    }

    /** Valida que o pedido contém pelo menos um produto da loja do vendedor autenticado. */
    private void validateStoreOwnership(Order order) {
        Profile store = getAuthenticatedProfile();
        boolean ownsProduct = order.getItems().stream()
                .anyMatch(i -> i.getProduct().getStore() != null
                        && i.getProduct().getStore().getId().equals(store.getId()));
        if (!ownsProduct) {
            throw new BusinessRuleException("Este pedido não contém produtos da sua loja.");
        }
    }

    /** Valida que o pedido está no status esperado antes de transitar. */
    private void validateTransition(Order order, EnumOrderStatus expected, EnumOrderStatus next) {
        if (order.getStatus() != expected) {
            throw new BusinessRuleException(
                    "Transição inválida: pedido está em " + order.getStatus() +
                    ", esperado " + expected + " para avançar para " + next
            );
        }
    }

    private Profile getAuthenticatedProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado para o usuário autenticado"));
    }

    private OrderReadDto toDto(Order order) {
        List<ItemOrderReadDto> items = order.getItems() == null ? List.of() :
                order.getItems().stream().map(this::itemToDto).toList();

        return new OrderReadDto(
                order.getId(),
                order.getProfile().getId(),
                order.getStatus(),
                order.getPrice(),
                items
        );
    }

    private ItemOrderReadDto itemToDto(ItemOrder item) {
        BigDecimal subtotal = item.getUnitPrice() != null
                ? item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                : BigDecimal.ZERO;
        return new ItemOrderReadDto(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                subtotal
        );
    }
}
