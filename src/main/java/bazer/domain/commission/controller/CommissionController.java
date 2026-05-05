package bazer.domain.commission.controller;

import bazer.domain.commission.dto.CommissionCreateDto;
import bazer.domain.commission.dto.CommissionReadDto;
import bazer.domain.commission.service.CommissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commissions")
@RequiredArgsConstructor
public class CommissionController {

    private final CommissionService commissionService;

    @PostMapping
    public ResponseEntity<CommissionReadDto> create(@RequestBody @Valid CommissionCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commissionService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CommissionReadDto>> findAll() {
        return ResponseEntity.ok(commissionService.findAll());
    }
}