package bazer.domain.notification;

import bazer.domain.order.entity.ItemOrder;
import bazer.domain.order.entity.Order;
import bazer.domain.profile.entity.Profile;
import bazer.domain.user.entity.EnumRole;
import bazer.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final ProfileRepository profileRepository;

    @Async
    public void sendOrderConfirmedToAdmins(Order order) {
        List<String> adminEmails = profileRepository.searchByRole(EnumRole.ADMIN, null)
                .stream()
                .map(p -> p.getUser().getUsername())
                .toList();

        if (adminEmails.isEmpty()) return;

        String subject = "Novo pedido pago — #" + order.getId();
        String body = buildEmailBody(order);

        adminEmails.forEach(email -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        });
    }

    private String buildEmailBody(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("PEDIDO PAGO — #").append(order.getId()).append("\n\n");

        sb.append("Comprador: ").append(order.getProfile().getName()).append("\n");
        if (order.getStore() != null) {
            sb.append("Loja: ").append(order.getStore().getName()).append("\n");
        }
        sb.append("\n--- ITENS ---\n");

        for (ItemOrder item : order.getItems()) {
            sb.append("• ").append(item.getProduct().getName())
              .append(" x").append(item.getQuantity())
              .append(" — R$ ").append(item.getUnitPrice()).append(" cada\n");
        }

        sb.append("\nFrete: R$ ").append(order.getShippingCost());
        sb.append("\nComissão Bazer: R$ ").append(order.getCommissionAmount());
        sb.append("\nTOTAL: R$ ").append(order.getPrice());
        sb.append("\n\nCEP destino: ").append(order.getPostalCodeDestination());
        sb.append("\n\nAcesse o painel para processar o pedido.");

        return sb.toString();
    }
}