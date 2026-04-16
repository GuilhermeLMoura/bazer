package bazer.domain.assessment_product.service;

import bazer.configuration.Exception.BusinessRuleException;
import bazer.domain.assessment_product.dto.AssessmentProductCreateDto;
import bazer.domain.assessment_product.dto.AssessmentProductReadDto;
import bazer.domain.assessment_product.entity.AssessmentProduct;
import bazer.domain.assessment_product.repository.AssessmentProductRepository;
import bazer.domain.product.entity.Product;
import bazer.domain.product.repository.ProductRepository;
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
public class AssessmentProductService {

    private final AssessmentProductRepository assessmentProductRepository;
    private final ProductRepository productRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public AssessmentProductReadDto create(AssessmentProductCreateDto dto) {
        Profile reviewer = getAuthenticatedProfile();

        if (reviewer.getUser().getRole() != EnumRole.COMPRADOR) {
            throw new BusinessRuleException("Apenas perfis COMPRADOR podem avaliar produtos.");
        }

        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + dto.productId()));

        AssessmentProduct assessment = new AssessmentProduct();
        assessment.setStarQuantity(dto.starQuantity());
        assessment.setComment(dto.comment());
        assessment.setProduct(product);
        assessment.setReviewer(reviewer);

        return toDto(assessmentProductRepository.save(assessment));
    }

    @Transactional(readOnly = true)
    public List<AssessmentProductReadDto> findByProduct(Long productId) {
        return assessmentProductRepository.findByProductId(productId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public AssessmentProductReadDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional
    public AssessmentProductReadDto update(Long id, AssessmentProductCreateDto dto) {
        AssessmentProduct assessment = getOrThrow(id);
        Profile reviewer = getAuthenticatedProfile();

        if (!assessment.getReviewer().getId().equals(reviewer.getId())) {
            throw new BusinessRuleException("Você não pode editar a avaliação de outro usuário.");
        }

        assessment.setStarQuantity(dto.starQuantity());
        assessment.setComment(dto.comment());
        return toDto(assessmentProductRepository.save(assessment));
    }

    @Transactional
    public void delete(Long id) {
        AssessmentProduct assessment = getOrThrow(id);
        Profile reviewer = getAuthenticatedProfile();

        if (!assessment.getReviewer().getId().equals(reviewer.getId())) {
            throw new BusinessRuleException("Você não pode excluir a avaliação de outro usuário.");
        }

        assessmentProductRepository.deleteById(id);
    }

    private Profile getAuthenticatedProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado para o usuário autenticado"));
    }

    private AssessmentProduct getOrThrow(Long id) {
        return assessmentProductRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada: " + id));
    }

    private AssessmentProductReadDto toDto(AssessmentProduct a) {
        return new AssessmentProductReadDto(
                a.getId(),
                a.getStarQuantity(),
                a.getComment(),
                a.getProduct().getId(),
                a.getReviewer() != null ? a.getReviewer().getId() : null,
                a.getReviewer() != null ? a.getReviewer().getName() : null
        );
    }
}
