package com.example.appmenu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "commandes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder                  // ← AJOUTÉ : permet d'utiliser Commande.builder()
@ToString                 // Optionnel, mais utile pour les logs
@EqualsAndHashCode        // Optionnel, mais recommandé pour les entités
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int tableNum;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String items; // JSON string

    @Column(nullable = false, length = 50)
    private String status = "en_attente";

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Double total;
}