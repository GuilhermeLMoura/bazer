package bazer.domain.category.service;

import bazer.domain.category.dto.CategoryCreateDto;
import bazer.domain.category.dto.CategoryReadDto;
import bazer.domain.category.entity.Category;
import bazer.domain.category.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryReadDto create(CategoryCreateDto dto) {
        Category category = new Category();
        category.setName(dto.name());
        return toDto(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryReadDto> findAll() {
        return categoryRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CategoryReadDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional
    public CategoryReadDto update(Long id, CategoryCreateDto dto) {
        Category category = getOrThrow(id);
        category.setName(dto.name());
        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Categoria não encontrada: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private Category getOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada: " + id));
    }

    private CategoryReadDto toDto(Category c) {
        return new CategoryReadDto(c.getId(), c.getName());
    }
}
