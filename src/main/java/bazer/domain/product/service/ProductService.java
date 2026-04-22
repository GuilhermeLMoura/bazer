package bazer.domain.product.service;

import bazer.domain.category.entity.Category;
import bazer.domain.category.repository.CategoryRepository;
import bazer.domain.product.dto.ProductCreateDto;
import bazer.domain.product.dto.ProductReadDto;
import bazer.domain.product.entity.Product;
import bazer.domain.product.entity.ProductImage;
import bazer.domain.product.repository.ProductImageRepository;
import bazer.domain.product.repository.ProductRepository;
import bazer.domain.profile.entity.Profile;
import bazer.domain.profile.repository.ProfileRepository;
import bazer.domain.profile.service.FileStorageService;
import bazer.domain.user.security.UserCustomDetail;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileRepository profileRepository;
    private final ProductImageRepository productImageRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public ProductReadDto create(ProductCreateDto dto) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada: " + dto.categoryId()));

        Profile store = getAuthenticatedProfile();

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setCategory(category);
        product.setStore(store);
        product.setPurchaseCount(0);

        return toDto(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductReadDto> findAll() {
        return productRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ProductReadDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ProductReadDto> findByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Categoria não encontrada: " + categoryId);
        }
        return productRepository.findByCategoryId(categoryId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductReadDto> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream().map(this::toDto).toList();
    }

    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public ProductReadDto update(Long id, ProductCreateDto dto) {
        Product product = getOrThrow(id);
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada: " + dto.categoryId()));

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setCategory(category);

        return toDto(productRepository.save(product));
    }

    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductReadDto> listByMostPurchased() {
        return productRepository.findAllByOrderByPurchaseCountDesc().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductReadDto> listByMostPurchasedByStore(Long storeId) {
        if (!profileRepository.existsById(storeId)) {
            throw new EntityNotFoundException("Loja não encontrada: " + storeId);
        }
        return productRepository.findByStoreIdOrderByPurchaseCountDesc(storeId).stream().map(this::toDto).toList();
    }

    @Transactional
    @PreAuthorize("hasRole('VENDEDOR')")
    public ProductReadDto addImage(Long productId, MultipartFile file) {
        Product product = getOrThrow(productId);
        String url = fileStorageService.store(file);

        ProductImage image = new ProductImage();
        image.setUrl(url);
        image.setProduct(product);
        productImageRepository.save(image);

        return toDto(getOrThrow(productId));
    }

    private Product getOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
    }

    private Profile getAuthenticatedProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserCustomDetail userDetail = (UserCustomDetail) auth.getPrincipal();
        return profileRepository.findByUserId(userDetail.getId())
                .orElseThrow(() -> new EntityNotFoundException("Perfil do vendedor não encontrado"));
    }

    public ProductReadDto toDto(Product p) {
        List<String> imageUrls = p.getImages() != null
                ? p.getImages().stream().map(ProductImage::getUrl).toList()
                : Collections.emptyList();

        return new ProductReadDto(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock(),
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getStore() != null ? p.getStore().getId() : null,
                p.getPurchaseCount(),
                imageUrls
        );
    }
}
