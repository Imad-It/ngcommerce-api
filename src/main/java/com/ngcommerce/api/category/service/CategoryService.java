package com.ngcommerce.api.category.service;

import com.ngcommerce.api.category.dto.CategoryRequest;
import com.ngcommerce.api.category.dto.CategoryResponse;
import com.ngcommerce.api.category.entity.Category;
import com.ngcommerce.api.category.repository.CategoryRepository;
import com.ngcommerce.api.common.exception.DuplicateResourceException;
import com.ngcommerce.api.common.exception.ResourceNotFoundException;
import com.ngcommerce.api.common.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository
                .findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        return toResponse(findCategoryById(id));
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with slug: " + slug
                        )
                );

        return toResponse(category);
    }

    public CategoryResponse createCategory(
            CategoryRequest request,
            MultipartFile image
    ) {

        if (categoryRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException(
                    "A category with this slug already exists"
            );
        }

        String imagePath = fileStorageService.store(
                image,
                "categories"
        );

        try {
            Category category = new Category(request.name(),
                    request.slug(),
                    imagePath
            );

            Category savedCategory = categoryRepository.save(category);

            return toResponse(savedCategory);

        } catch (RuntimeException ex) {
            fileStorageService.delete(imagePath);
            throw ex;
        }
    }

    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request,
            MultipartFile image
    ) {

        Category existing = findCategoryById(id);

        categoryRepository.findBySlug(request.slug())
                .filter(category -> !category.getId().equals(id))
                .ifPresent(category -> {
                    throw new DuplicateResourceException(
                            "A category with this slug already exists"
                    );
                });

        String oldImagePath = existing.getImagePath();
        String newImagePath = oldImagePath;

        if (image != null && !image.isEmpty()) {
            newImagePath = fileStorageService.store(
                    image,
                    "categories"
            );
        }

        try {
            existing.update(
                    request.name(),
                    request.slug(),
                    newImagePath
            );

            Category updated = categoryRepository.save(existing);

            return toResponse(updated);

        } catch (RuntimeException ex) {
            if (!newImagePath.equals(oldImagePath)) {
                fileStorageService.delete(newImagePath);
            }
            throw ex;
        }
    }

    public void deleteCategory(Long id) {
        Category category = findCategoryById(id);

        String imagePath = category.getImagePath();

        categoryRepository.delete(category);

        if (imagePath != null) {
            fileStorageService.delete(imagePath);
        }
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        )
                );
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getImagePath(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}