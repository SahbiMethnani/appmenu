package com.example.appmenu.util;

import com.example.appmenu.entity.Plat;
import com.example.appmenu.entity.RestaurantConfig;
import com.example.appmenu.repository.PlatRepository;
import com.example.appmenu.repository.RestaurantConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PlatRepository platRepository;
    private final RestaurantConfigRepository configRepository;

    @Override
    public void run(String... args) {
        if (platRepository.count() == 0) {
            platRepository.saveAll(java.util.List.of(
                    Plat.builder().nom("Salade César").description("Salade verte, croûtons, parmesan, sauce césar").prix(12.50).categorie("Entrées").build(),
                    Plat.builder().nom("Soupe du jour").description("Soupe préparée avec les légumes de saison").prix(8.00).categorie("Entrées").build(),
                    Plat.builder().nom("Steak frites").description("Steak de bœuf grillé avec frites maison").prix(22.00).categorie("Plats").build(),
                    Plat.builder().nom("Saumon grillé").description("Saumon grillé avec légumes de saison").prix(24.00).categorie("Plats").build(),
                    Plat.builder().nom("Pizza Margherita").description("Tomate, mozzarella, basilic").prix(16.00).categorie("Plats").build(),
                    Plat.builder().nom("Tiramisu").description("Dessert italien traditionnel").prix(7.50).categorie("Desserts").build(),
                    Plat.builder().nom("Tarte aux pommes").description("Tarte maison aux pommes").prix(6.50).categorie("Desserts").build(),
                    Plat.builder().nom("Coca Cola").description("Boisson gazeuse 33cl").prix(3.50).categorie("Boissons").build(),
                    Plat.builder().nom("Eau minérale").description("Eau plate ou pétillante 50cl").prix(2.50).categorie("Boissons").build()
            ));
        }

        if (configRepository.count() == 0) {
            configRepository.save(new RestaurantConfig(1L, 10));
        }
    }
}