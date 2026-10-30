package com.ngcommerce.api.category.service;

import com.ngcommerce.api.category.dto.CategoryRequest;
import com.ngcommerce.api.category.dto.CategoryResponse;
import com.ngcommerce.api.category.entity.Category;
import com.ngcommerce.api.category.exception.DuplicateResourceException;
import com.ngcommerce.api.category.exception.ResourceNotFoundException;
import com.ngcommerce.api.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::toResponse)
                .toList();

    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        return toResponse(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return toResponse(category);
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException("A category with this slug already exists");
        }

        Category category = new Category();
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setImage(request.image());

        Category savedCategory = categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category existing = findCategoryById(id);

        categoryRepository.findBySlug(request.slug())
                .filter(category -> !category.getId().equals(id))
                .ifPresent(category -> {
                    throw new DuplicateResourceException("A category with this slug already exists");
                });

        existing.setName(request.name());
        existing.setSlug(request.slug());
        existing.setImage(request.image());

        Category updatedCategory = categoryRepository.save(existing);

        return toResponse(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = findCategoryById(id);
        categoryRepository.delete(category);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getImage(),
                category.getCreationAt(),
                category.getUpdatedAt()
        );
    }
}
