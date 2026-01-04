package com.example.appmenu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorie_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategorieImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String categorie;


    @Column(name = "image_base64", columnDefinition = "TEXT")
    private String imageBase64;
}