package bazer.domain.assessment_profile.controller;

import bazer.domain.assessment_profile.dto.AssessmentProfileCreateDto;
import bazer.domain.assessment_profile.dto.AssessmentProfileReadDto;
import bazer.domain.assessment_profile.dto.AssessmentProfileUpdateDto;
import bazer.domain.assessment_profile.service.AssessmentProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessment-profiles")
@RequiredArgsConstructor
public class AssessmentProfileController {

    private final AssessmentProfileService assessmentProfileService;

    @PostMapping
    public ResponseEntity<AssessmentProfileReadDto> create(@RequestBody @Valid AssessmentProfileCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentProfileService.create(dto));
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<AssessmentProfileReadDto>> findByProfile(@PathVariable Long profileId) {
        return ResponseEntity.ok(assessmentProfileService.findByProfile(profileId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentProfileReadDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentProfileService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssessmentProfileReadDto> update(@PathVariable Long id,
                                                           @RequestBody @Valid AssessmentProfileUpdateDto dto) {
        return ResponseEntity.ok(assessmentProfileService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assessmentProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
