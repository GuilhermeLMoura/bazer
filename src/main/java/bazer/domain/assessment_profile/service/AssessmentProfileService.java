package bazer.domain.assessment_profile.service;

import bazer.configuration.Exception.BusinessRuleException;
import bazer.domain.assessment_profile.dto.AssessmentProfileCreateDto;
import bazer.domain.assessment_profile.dto.AssessmentProfileReadDto;
import bazer.domain.assessment_profile.dto.AssessmentProfileUpdateDto;
import bazer.domain.assessment_profile.entity.AssessmentProfile;
import bazer.domain.assessment_profile.repository.AssessmentProfileRepository;
import bazer.domain.profile.entity.Profile;
import bazer.domain.profile.repository.ProfileRepository;
import bazer.domain.user.entity.EnumRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentProfileService {

    private final AssessmentProfileRepository assessmentProfileRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public AssessmentProfileReadDto create(AssessmentProfileCreateDto dto) {
        Profile reviewer = getAuthenticatedProfile();

        // Regra: apenas COMPRADOR pode avaliar lojas
        if (reviewer.getUser().getRole() != EnumRole.COMPRADOR) {
            throw new BusinessRuleException("Apenas perfis COMPRADOR podem avaliar vendedores.");
        }

        Profile target = profileRepository.findById(dto.profileId())
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado: " + dto.profileId()));

        // Regra: só é possível avaliar perfis VENDEDOR
        if (target.getUser().getRole() != EnumRole.VENDEDOR) {
            throw new BusinessRuleException("Somente perfis VENDEDOR podem ser avaliados.");
        }

        AssessmentProfile assessment = new AssessmentProfile();
        assessment.setStarQuantity(dto.starQuantity());
        assessment.setComment(dto.comment());
        assessment.setProfile(target);
        assessment.setReviewer(reviewer);

        return toReadDto(assessmentProfileRepository.save(assessment));
    }

    @Transactional(readOnly = true)
    public List<AssessmentProfileReadDto> findByProfile(Long profileId) {
        return assessmentProfileRepository.findByProfileId(profileId)
                .stream().map(this::toReadDto).toList();
    }

    @Transactional(readOnly = true)
    public AssessmentProfileReadDto findById(Long id) {
        return toReadDto(getOrThrow(id));
    }

    @Transactional
    public AssessmentProfileReadDto update(Long id, AssessmentProfileUpdateDto dto) {
        AssessmentProfile assessment = getOrThrow(id);
        Profile reviewer = getAuthenticatedProfile();

        if (!assessment.getReviewer().getId().equals(reviewer.getId())) {
            throw new BusinessRuleException("Você não pode editar a avaliação de outro usuário.");
        }

        assessment.setStarQuantity(dto.starQuantity());
        return toReadDto(assessmentProfileRepository.save(assessment));
    }

    @Transactional
    public void delete(Long id) {
        AssessmentProfile assessment = getOrThrow(id);
        Profile reviewer = getAuthenticatedProfile();

        if (!assessment.getReviewer().getId().equals(reviewer.getId())) {
            throw new BusinessRuleException("Você não pode excluir a avaliação de outro usuário.");
        }

        assessmentProfileRepository.deleteById(id);
    }

    private Profile getAuthenticatedProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado para o usuário autenticado"));
    }

    private AssessmentProfile getOrThrow(Long id) {
        return assessmentProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada: " + id));
    }

    private AssessmentProfileReadDto toReadDto(AssessmentProfile a) {
        return new AssessmentProfileReadDto(
                a.getId(),
                a.getStarQuantity(),
                a.getComment(),
                a.getProfile().getId(),
                a.getReviewer() != null ? a.getReviewer().getId() : null,
                a.getReviewer() != null ? a.getReviewer().getName() : null
        );
    }
}
