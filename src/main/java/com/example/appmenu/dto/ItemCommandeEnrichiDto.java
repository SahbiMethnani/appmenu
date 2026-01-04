package com.example.appmenu.dto;

public record ItemCommandeEnrichiDto(
        Long platId,
        int quantite,
        String nom,
        String description,
        double prix,
        String categorie,
        String imageUrl
) {

}
