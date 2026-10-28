
package com.ngcommerce.api.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 150)
    private String name;

    @NotBlank(message = "Category slug is required")
    @Size(max = 180)
    private String slug;

    private String image;
}

