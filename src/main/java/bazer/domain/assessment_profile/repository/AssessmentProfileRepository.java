package bazer.domain.assessment_profile.repository;

import bazer.domain.assessment_profile.entity.AssessmentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentProfileRepository extends JpaRepository<AssessmentProfile, Long> {

    List<AssessmentProfile> findByProfileId(Long profileId);
}
