package bazer.domain.product.controller;

import bazer.domain.product.dto.ProductCreateDto;
import bazer.domain.product.dto.ProductReadDto;
import bazer.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductReadDto> create(@RequestBody @Valid ProductCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProductReadDto>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductReadDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductReadDto>> findByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.findByCategory(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductReadDto>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.findByName(name));
    }

    @GetMapping("/most-purchased")
    public ResponseEntity<List<ProductReadDto>> listByMostPurchased() {
        return ResponseEntity.ok(productService.listByMostPurchased());
    }

    @GetMapping("/most-purchased/store/{storeId}")
    public ResponseEntity<List<ProductReadDto>> listByMostPurchasedByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(productService.listByMostPurchasedByStore(storeId));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductReadDto> addImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addImage(id, image));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductReadDto> update(@PathVariable Long id,
                                                 @RequestBody @Valid ProductCreateDto dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
