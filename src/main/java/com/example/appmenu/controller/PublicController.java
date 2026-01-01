package com.example.appmenu.controller;  //

import com.example.appmenu.dto.*;
import com.example.appmenu.entity.Commande;
import com.example.appmenu.entity.RestaurantConfig;
import com.example.appmenu.repository.*;
import com.example.appmenu.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PublicController {

    private final PlatRepository platRepository;
    private final CommandeRepository commandeRepository;
    private final CategorieImageRepository categorieImageRepository;
    private final RestaurantConfigRepository configRepository;
    private final CommandeService commandeService;

    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of(
                "message", "API Restaurant - Service de commandes",
                "version", "2.0.0",
                "status", "operational"
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "healthy", "database", "connected");
    }

    @GetMapping("/menu")
    public List<PlatDto> getMenu() {
        return platRepository.findAll().stream()
                .map(p -> new PlatDto(p.getId(), p.getNom(), p.getDescription(),
                        p.getPrix(), p.getCategorie(), p.getImageUrl()))
                .toList();
    }

    @PostMapping("/commande")
    public ResponseEntity<Map<String, Object>> createCommande(@Valid @RequestBody CommandeCreateDto dto) {
        RestaurantConfig config = configRepository.findById(1L)
                .orElse(new RestaurantConfig(null, 10));

        if (dto.tableNum() > config.getNombreTables()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("detail", "Numéro de table invalide (max: " + config.getNombreTables() + ")"));
        }

        double total = commandeService.calculateTotal(dto.items());

        String itemsJson;
        try {
            itemsJson = commandeService.itemsToJson(dto.items());
        } catch (Exception e) {  // JsonProcessingException est une Exception
            return ResponseEntity.internalServerError()
                    .body(Map.of("detail", "Erreur lors de la sérialisation des items"));
        }

        Commande commande = Commande.builder()
                .tableNum(dto.tableNum())
                .items(itemsJson)
                .total(Math.round(total * 100.0) / 100.0)
                .status("en_attente")
                .build();

        commande = commandeRepository.save(commande);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Commande créée avec succès");
        response.put("commande_id", commande.getId());
        response.put("status", "en_attente");
        response.put("total", commande.getTotal());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/commande/{tableNum}")
    public CommandeDto getCommandeByTable(@PathVariable int tableNum) {
        return commandeRepository.findTopByTableNumOrderByCreatedAtDesc(tableNum)
                .map(c -> {
                    try {
                        return new CommandeDto(
                                c.getId(),
                                c.getTableNum(),
                                commandeService.jsonToItems(c.getItems()),
                                c.getStatus(),
                                c.getCreatedAt(),
                                c.getTotal()
                        );
                    } catch (Exception e) {
                        // En cas d'erreur de désérialisation, on retourne une liste vide
                        return new CommandeDto(
                                c.getId(),
                                c.getTableNum(),
                                List.of(),
                                c.getStatus(),
                                c.getCreatedAt(),
                                c.getTotal()
                        );
                    }
                })
                .orElse(null);
    }

    @GetMapping("/categories/images")
    public Map<String, String> getCategoriesImages() {
        Map<String, String> map = new HashMap<>();
        categorieImageRepository.findAll()
                .forEach(img -> map.put(img.getCategorie(), img.getImageBase64()));
        return map;
    }

    @GetMapping("/config/tables")
    public Map<String, Integer> getTableConfig() {
        int tables = configRepository.findById(1L)
                .map(RestaurantConfig::getNombreTables)
                .orElse(10);
        return Map.of("nombre_tables", tables);
    }
}