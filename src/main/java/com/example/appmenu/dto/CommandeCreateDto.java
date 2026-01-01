package com.example.appmenu.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record CommandeCreateDto(
        @NotNull @Positive @Max(200) Integer tableNum,
        @NotEmpty @Size(max = 50) List<ItemCommandeDto> items
) {}