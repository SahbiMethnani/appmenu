package com.example.appmenu.repository;

import com.example.appmenu.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    Optional<Commande> findTopByTableNumOrderByCreatedAtDesc(int tableNum);
}