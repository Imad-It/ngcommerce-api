
package com.ngcommerce.api.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String slug;
    private String image;
    private Instant creationAt;
    private Instant updatedAt;
}
