package com.example.appmenu.dto;

import jakarta.validation.constraints.*;

public record ItemCommandeDto(
        @NotNull @Positive Long platId,
        @NotNull @Positive @Max(100) Integer quantite
) {}