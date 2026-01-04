package com.example.appmenu.dto;

import jakarta.validation.constraints.*;

public record PlatDto(
        Long id,
        @NotBlank @Size(max = 255) String nom,
        @Size(max = 1000) String description,
        @Positive @Max(10000) double prix,
        @NotBlank @Size(max = 100) String categorie,
        //@Size(max = 500)
        String imageUrl
) {}