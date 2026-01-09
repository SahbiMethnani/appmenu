package com.example.appmenu.repository;

import com.example.appmenu.entity.CategorieImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategorieImageRepository extends JpaRepository<CategorieImage, Long> {
    Optional<CategorieImage> findByCategorie(String categorie);
}