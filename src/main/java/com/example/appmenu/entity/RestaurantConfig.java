package com.example.appmenu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RestaurantConfig {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int nombreTables = 10;
}