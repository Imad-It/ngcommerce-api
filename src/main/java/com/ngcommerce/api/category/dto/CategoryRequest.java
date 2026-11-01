package com.ngcommerce.api.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record CategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 150)
        String name,

        @NotBlank(message = "Category slug is required")
        @Size(max = 180)
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "Slug must contain only lowercase letters, numbers and hyphens"
        )
        String slug,

        String image
) {
}

