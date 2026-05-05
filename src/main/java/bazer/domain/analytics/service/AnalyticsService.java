package bazer.domain.analytics.service;

import bazer.domain.analytics.dto.*;
import bazer.domain.order.entity.EnumOrderStatus;
import bazer.domain.order.entity.ItemOrder;
import bazer.domain.order.entity.Order;
import bazer.domain.order.repository.OrderRepository;
import bazer.domain.profile.entity.Profile;
import bazer.domain.profile.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private static final List<EnumOrderStatus> REVENUE_STATUSES = List.of(
            EnumOrderStatus.CONFIRMED,
            EnumOrderStatus.PROCESSING,
            EnumOrderStatus.SHIPPED,
            EnumOrderStatus.DELIVERED
    );

    private final OrderRepository orderRepository;
    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('VENDEDOR')")
    public StoreAnalyticsDto getStoreAnalytics() {
        Profile store = getAuthenticatedProfile();
        return buildStoreAnalytics(store.getId());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public StoreAnalyticsDto getStoreAnalyticsById(Long profileId) {
        return buildStoreAnalytics(profileId);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public AdminAnalyticsDto getAdminAnalytics(Integer year) {
        int targetYear = year != null ? year : LocalDateTime.now().getYear();
        List<Order> all = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != EnumOrderStatus.PENDING)
                .toList();

        List<Order> revenueOrders = all.stream()
                .filter(o -> REVENUE_STATUSES.contains(o.getStatus()))
                .toList();

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        BigDecimal platformRevenue = sum(revenueOrders.stream().map(Order::getPrice).toList());

        BigDecimal monthCommission = revenueOrders.stream()
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().isAfter(startOfMonth))
                .map(o -> o.getCommissionAmount() != null ? o.getCommissionAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal yearlyCommission = revenueOrders.stream()
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().getYear() == targetYear)
                .map(o -> o.getCommissionAmount() != null ? o.getCommissionAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long deliveredOrders = all.stream()
                .filter(o -> o.getStatus() == EnumOrderStatus.DELIVERED)
                .count();

        Map<String, Long> ordersByStatus = all.stream()
                .collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()));

        List<StoreRankingDto> topStores = revenueOrders.stream()
                .filter(o -> o.getStore() != null)
                .collect(Collectors.groupingBy(o -> o.getStore()))
                .entrySet().stream()
                .map(e -> new StoreRankingDto(
                        e.getKey().getId(),
                        e.getKey().getName(),
                        sum(e.getValue().stream().map(Order::getPrice).toList()),
                        e.getValue().size()
                ))
                .sorted(Comparator.comparing(StoreRankingDto::revenue).reversed())
                .limit(10)
                .toList();

        List<BuyerRankingDto> topBuyers = revenueOrders.stream()
                .collect(Collectors.groupingBy(o -> o.getProfile()))
                .entrySet().stream()
                .map(e -> new BuyerRankingDto(
                        e.getKey().getId(),
                        e.getKey().getName(),
                        sum(e.getValue().stream().map(Order::getPrice).toList()),
                        e.getValue().size()
                ))
                .sorted(Comparator.comparing(BuyerRankingDto::totalSpent).reversed())
                .limit(10)
                .toList();

        return new AdminAnalyticsDto(
                all.size(),
                deliveredOrders,
                platformRevenue,
                monthCommission,
                yearlyCommission,
                targetYear,
                ordersByStatus,
                topStores,
                topBuyers
        );
    }

    private StoreAnalyticsDto buildStoreAnalytics(Long storeId) {
        List<Order> all = orderRepository.findByStoreIdAndStatusNot(storeId, EnumOrderStatus.PENDING);

        List<Order> revenueOrders = all.stream()
                .filter(o -> REVENUE_STATUSES.contains(o.getStatus()))
                .toList();

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        BigDecimal totalRevenue = sum(revenueOrders.stream().map(Order::getPrice).toList());

        BigDecimal monthRevenue = revenueOrders.stream()
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().isAfter(startOfMonth))
                .map(Order::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalOrders = revenueOrders.size();
        long monthOrders = revenueOrders.stream()
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().isAfter(startOfMonth))
                .count();

        BigDecimal averageTicket = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long uniqueBuyers = revenueOrders.stream()
                .map(o -> o.getProfile().getId())
                .distinct()
                .count();

        BigDecimal totalCommissionPaid = revenueOrders.stream()
                .map(o -> o.getCommissionAmount() != null ? o.getCommissionAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthCommissionPaid = revenueOrders.stream()
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().isAfter(startOfMonth))
                .map(o -> o.getCommissionAmount() != null ? o.getCommissionAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> ordersByStatus = all.stream()
                .collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()));

        List<TopProductDto> topProducts = revenueOrders.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(i -> i.getProduct().getName()))
                .entrySet().stream()
                .map(e -> {
                    int qty = e.getValue().stream().mapToInt(ItemOrder::getQuantity).sum();
                    BigDecimal rev = e.getValue().stream()
                            .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new TopProductDto(e.getKey(), qty, rev);
                })
                .sorted(Comparator.comparingInt(TopProductDto::totalQuantity).reversed())
                .limit(5)
                .toList();

        List<MonthlyRevenueDto> last6Months = revenueOrders.stream()
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().isAfter(sixMonthsAgo))
                .collect(Collectors.groupingBy(o -> YearMonth.from(o.getCreatedAt())))
                .entrySet().stream()
                .map(e -> new MonthlyRevenueDto(
                        e.getKey().getMonthValue(),
                        e.getKey().getYear(),
                        sum(e.getValue().stream().map(Order::getPrice).toList()),
                        e.getValue().size()
                ))
                .sorted(Comparator.comparing(m -> YearMonth.of(m.year(), m.month())))
                .toList();

        return new StoreAnalyticsDto(
                totalOrders,
                monthOrders,
                totalRevenue,
                monthRevenue,
                averageTicket,
                uniqueBuyers,
                totalCommissionPaid,
                monthCommissionPaid,
                ordersByStatus,
                topProducts,
                last6Months
        );
    }

    private BigDecimal sum(List<BigDecimal> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Profile getAuthenticatedProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado"));
    }
}