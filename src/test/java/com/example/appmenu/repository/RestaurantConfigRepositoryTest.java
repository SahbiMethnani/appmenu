package com.example.appmenu.repository;

import com.example.appmenu.entity.RestaurantConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests du repository RestaurantConfig")
class RestaurantConfigRepositoryTest {

    @Autowired
    private RestaurantConfigRepository restaurantConfigRepository;

    @BeforeEach
    void setUp() {
        restaurantConfigRepository.deleteAll();
    }

    @Test
    @DisplayName("Doit sauvegarder et retrouver une configuration")
    void saveAndFind() {
        // Arrange
        RestaurantConfig config = RestaurantConfig.builder()
                .nombreTables(25)
                .build();

        // Act
        RestaurantConfig saved = restaurantConfigRepository.save(config);
        Optional<RestaurantConfig> found = restaurantConfigRepository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getNombreTables()).isEqualTo(25);
    }

    @Test
    @DisplayName("Doit utiliser la valeur par défaut de 10 tables")
    void defaultNombreTables() {
        // Arrange
        RestaurantConfig config = RestaurantConfig.builder().build();

        // Act
        RestaurantConfig saved = restaurantConfigRepository.save(config);

        // Assert
        assertThat(saved.getNombreTables()).isEqualTo(10);
    }

    @Test
    @DisplayName("Doit mettre à jour le nombre de tables")
    void updateNombreTables() {
        // Arrange
        RestaurantConfig config = RestaurantConfig.builder()
                .nombreTables(15)
                .build();
        RestaurantConfig saved = restaurantConfigRepository.save(config);

        // Act
        saved.setNombreTables(30);
        restaurantConfigRepository.save(saved);

        // Assert
        Optional<RestaurantConfig> updated = restaurantConfigRepository.findById(saved.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getNombreTables()).isEqualTo(30);
    }

    @Test
    @DisplayName("Doit retrouver la config avec l'ID 1")
    void findById1() {
        // Arrange
        RestaurantConfig config = new RestaurantConfig();
        config.setNombreTables(20);
        RestaurantConfig saved = restaurantConfigRepository.save(config);

        // Act
        Optional<RestaurantConfig> found = restaurantConfigRepository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getNombreTables()).isEqualTo(20);
    }

    @Test
    @DisplayName("Doit supprimer une configuration")
    void deleteConfig() {
        // Arrange
        RestaurantConfig config = restaurantConfigRepository.save(
                RestaurantConfig.builder().nombreTables(12).build()
        );
        Long id = config.getId();

        // Act
        restaurantConfigRepository.deleteById(id);

        // Assert
        assertThat(restaurantConfigRepository.existsById(id)).isFalse();
    }

    @Test
    @DisplayName("Doit accepter des valeurs de tables entre 1 et 200")
    void validTableRange() {
        // Arrange & Act
        RestaurantConfig min = restaurantConfigRepository.save(
                RestaurantConfig.builder().nombreTables(1).build()
        );
        RestaurantConfig max = restaurantConfigRepository.save(
                RestaurantConfig.builder().nombreTables(200).build()
        );

        // Assert
        assertThat(min.getNombreTables()).isEqualTo(1);
        assertThat(max.getNombreTables()).isEqualTo(200);
    }

    @Test
    @DisplayName("Doit compter les configurations")
    void countConfigs() {
        // Arrange
        assertThat(restaurantConfigRepository.count()).isZero();

        // Act
        restaurantConfigRepository.save(RestaurantConfig.builder().nombreTables(10).build());
        restaurantConfigRepository.save(RestaurantConfig.builder().nombreTables(15).build());

        // Assert
        assertThat(restaurantConfigRepository.count()).isEqualTo(2);
    }
}