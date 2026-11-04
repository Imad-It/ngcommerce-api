package com.ngcommerce.api.category.controller;

import com.ngcommerce.api.category.dto.CategoryRequest;
import com.ngcommerce.api.category.dto.CategoryResponse;
import com.ngcommerce.api.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(
                categoryService.getAllCategories()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                categoryService.getCategoryById(id)
        );
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryResponse> getCategoryBySlug(
            @PathVariable String slug
    ) {
        return ResponseEntity.ok(
                categoryService.getCategoryBySlug(slug)
        );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @ModelAttribute CategoryRequest request,
            @RequestParam("image") MultipartFile image
    ) {
        CategoryResponse created =
                categoryService.createCategory(request, image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CategoryResponse> updateCategory(

            @PathVariable Long id,

            @Valid @RequestPart("data")
            CategoryRequest request,

            @RequestPart(value = "image", required = false)
            MultipartFile image

    ) {

        return ResponseEntity.ok(
                categoryService.updateCategory(id, request, image)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id
    ) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}