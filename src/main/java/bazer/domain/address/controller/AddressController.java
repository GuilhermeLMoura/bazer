package bazer.domain.address.controller;

import bazer.domain.address.dto.AddressCreateDto;
import bazer.domain.address.dto.AddressReadDto;
import bazer.domain.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressReadDto> create(@RequestBody AddressCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<AddressReadDto>> findAll() {
        return ResponseEntity.ok(addressService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressReadDto> update(@PathVariable Long id, @RequestBody AddressCreateDto dto) {
        return ResponseEntity.ok(addressService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}