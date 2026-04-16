package bazer.domain.assessment_product.repository;

import bazer.domain.assessment_product.entity.AssessmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentProductRepository extends JpaRepository<AssessmentProduct, Long> {

    List<AssessmentProduct> findByProductId(Long productId);
}
