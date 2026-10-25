package com.ngcommerce.api.category.service;

import com.ngcommerce.api.category.entity.Category;
import com.ngcommerce.api.category.repository.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll(
                Sort.by(Sort.Direction.ASC, "id")
        );
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Category not found with id: " + id
                        )
                );
    }

    public Category createCategory(Category category) {
        category.setId(null);

        if (categoryRepository.existsBySlug(category.getSlug())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A category with this slug already exists"
            );
        }

        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category request) {
        Category existing = getCategoryById(id);

        categoryRepository.findBySlug(request.getSlug())
                .filter(category ->
                        !category.getId().equals(id))
                .ifPresent(category -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "A category with this slug already exists"
                    );
                });

        existing.setName(request.getName());
        existing.setSlug(request.getSlug());
        existing.setImage(request.getImage());

        return categoryRepository.save(existing);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }


    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"
                ));
    }
}
