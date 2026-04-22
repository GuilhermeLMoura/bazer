package bazer.domain.profile.repository;

import bazer.domain.profile.entity.Profile;
import bazer.domain.user.entity.EnumRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

    Optional<Profile> findByUserUsername(String username);

    @Query("SELECT p FROM Profile p WHERE p.user.role = :role AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Profile> searchByRole(@Param("role") EnumRole role, @Param("name") String name);
}
