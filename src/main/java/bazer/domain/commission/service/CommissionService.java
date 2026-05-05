package bazer.domain.commission.service;

import bazer.domain.commission.dto.CommissionCreateDto;
import bazer.domain.commission.dto.CommissionReadDto;
import bazer.domain.commission.entity.Commission;
import bazer.domain.commission.repository.CommissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final CommissionRepository commissionRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public CommissionReadDto create(CommissionCreateDto dto) {
        Commission commission = new Commission();
        commission.setName(dto.name());
        commission.setRate(dto.rate());
        return toDto(commissionRepository.save(commission));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<CommissionReadDto> findAll() {
        return commissionRepository.findAll().stream().map(this::toDto).toList();
    }

    public Commission getOrThrow(Long id) {
        return commissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comissão não encontrada: " + id));
    }

    private CommissionReadDto toDto(Commission c) {
        return new CommissionReadDto(c.getId(), c.getName(), c.getRate());
    }
}