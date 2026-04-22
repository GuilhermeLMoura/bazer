package bazer.integration.melhorenvio.repository;

import bazer.integration.melhorenvio.entity.MelhorEnvioToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MelhorEnvioTokenRepository extends JpaRepository<MelhorEnvioToken, Long> {
    Optional<MelhorEnvioToken> findTopByOrderByCreatedAtDesc();
}
