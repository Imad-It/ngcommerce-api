package com.ngcommerce.api.category.dto;

import java.time.Instant;

public record CategoryResponse(

        Long id,
        String name,
        String slug,
        String image,
        Instant creationAt,
        Instant updatedAt
) {
}