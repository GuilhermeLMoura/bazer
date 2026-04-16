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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    /**
     * Retorna o carrinho ativo (PENDING) do usuário logado.
     * Se não existir, cria um novo automaticamente.
     */
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

    /**
     * Adiciona um produto ao carrinho (Order PENDING).
     * Se o produto já estiver no carrinho, incrementa a quantidade.
     */
    @Transactional
    public OrderReadDto addItem(CartItemCreateDto dto) {
        Profile profile = getAuthenticatedProfile();
        Order cart = getActiveCart(profile.getId());

        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + dto.productId()));

        if (product.getStock() < dto.quantity()) {
            throw new BusinessRuleException("Estoque insuficiente. Disponível: " + product.getStock());
        }

        // Verifica se o produto já está no carrinho
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

    /**
     * Remove um item do carrinho.
     */
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

    /**
     * Checkout: move o carrinho (PENDING) para AGUARDANDO_PAGAMENTO.
     * A partir daqui o Order vira um pedido real.
     */
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

    /**
     * Lista todos os pedidos reais (status != PENDING) do usuário logado.
     */
    @Transactional(readOnly = true)
    public List<OrderReadDto> listOrders() {
        Profile profile = getAuthenticatedProfile();
        return orderRepository.findByProfileIdAndStatusNot(profile.getId(), EnumOrderStatus.PENDING)
                .stream().map(this::toDto).toList();
    }

    /**
     * Busca um pedido pelo ID.
     */
    @Transactional(readOnly = true)
    public OrderReadDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + id));
        return toDto(order);
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
