package com.example.appmenu.dto;

import jakarta.validation.constraints.*;

public record CategorieImageDto(
        @NotBlank @Size(max = 100) String categorie,
        @NotBlank @Size(max = 5_000_000) String imageBase64
) {
    public CategorieImageDto {
        if (!imageBase64.startsWith("data:image/")) {
            throw new IllegalArgumentException("Format image invalide");
        }
    }
}