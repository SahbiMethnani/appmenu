package com.example.appmenu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plats")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Plat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private double prix;

    @Column(nullable = false, length = 100)
    private String categorie;

    @Column(length = 500)
    private String imageUrl;
}