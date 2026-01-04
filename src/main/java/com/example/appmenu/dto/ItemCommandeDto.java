package com.example.appmenu.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO pour un item de commande
 * Accepte à la fois "platId" (camelCase) et "plat_id" (snake_case)
 */
public record ItemCommandeDto(

        @JsonProperty("platId")
        @JsonAlias({"plat_id"})
        @NotNull(message = "L'ID du plat est requis")
        @Min(value = 1, message = "L'ID du plat doit être positif")
        Long platId,

        @JsonProperty("quantite")
        @NotNull(message = "La quantité est requise")
        @Min(value = 1, message = "La quantité minimum est 1")
        @Max(value = 100, message = "La quantité maximum est 100")
        Integer quantite
) {}