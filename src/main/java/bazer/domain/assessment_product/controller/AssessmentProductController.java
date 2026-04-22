package bazer.domain.assessment_product.controller;

import bazer.domain.assessment_product.dto.AssessmentProductCreateDto;
import bazer.domain.assessment_product.dto.AssessmentProductReadDto;
import bazer.domain.assessment_product.service.AssessmentProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessment-products")
@RequiredArgsConstructor
public class AssessmentProductController {

    private final AssessmentProductService assessmentProductService;

    @PostMapping
    public ResponseEntity<AssessmentProductReadDto> create(@RequestBody @Valid AssessmentProductCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentProductService.create(dto));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<AssessmentProductReadDto>> findByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(assessmentProductService.findByProduct(productId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentProductReadDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentProductService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssessmentProductReadDto> update(@PathVariable Long id,
                                                           @RequestBody @Valid AssessmentProductCreateDto dto) {
        return ResponseEntity.ok(assessmentProductService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assessmentProductService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
