package com.example.appmenu.controller;

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

    @GetMapping("/commandes/client/{clientId}")
    public List<CommandeEnrichieDto> getCommandesByClient(@PathVariable String clientId) {
        return commandeRepository.findAllByClientId(clientId)
            .stream()
            .map(c -> new CommandeEnrichieDto(
                c.getId(),
                c.getTableNum(),
                commandeService.jsonToItemsEnrichis(c.getItems()),
                c.getStatus(),
                c.getCreatedAt(),
                c.getTotal()
            ))
            .toList();
    }

    @PostMapping("/commande")
    public ResponseEntity<CommandeEnrichieDto> createCommande(@Valid @RequestBody CommandeCreateDto dto) {
        // Get the first configuration or use default
        RestaurantConfig config = configRepository.findAll().stream()
                .findFirst()
                .orElse(new RestaurantConfig(null, 10));

        if (dto.tableNum() > config.getNombreTables()) {
            return ResponseEntity.badRequest().build();
        }

        double total = commandeService.calculateTotal(dto.items());

        String itemsJson;
        try {
            itemsJson = commandeService.itemsToJson(dto.items());
        } catch (Exception e) {  // JsonProcessingException est une Exception
            return ResponseEntity.internalServerError().build();
        }

        Commande commande = Commande.builder()
                .tableNum(dto.tableNum())
                .items(itemsJson)
                .total(Math.round(total * 100.0) / 100.0)
                .status("en_attente")
                .build();

        commande = commandeRepository.save(commande);

        // Build enriched DTO to return immediately
        CommandeEnrichieDto dtoResp = new CommandeEnrichieDto(
                commande.getId(),
                commande.getTableNum(),
                commandeService.jsonToItemsEnrichis(commande.getItems()),
                commande.getStatus(),
                commande.getCreatedAt(),
                commande.getTotal()
        );

        return ResponseEntity.ok(dtoResp);
    }

    @GetMapping("/commande/{tableNum}")
    public CommandeEnrichieDto getCommandeByTable(@PathVariable int tableNum) {
        return commandeRepository.findTopByTableNumOrderByCreatedAtDesc(tableNum)
                .map(c -> {
                    try {
                        return new CommandeEnrichieDto(
                                c.getId(),
                                c.getTableNum(),
                                commandeService.jsonToItemsEnrichis(c.getItems()),
                                c.getStatus(),
                                c.getCreatedAt(),
                                c.getTotal()
                        );
                    } catch (Exception e) {
                        // En cas d'erreur de désérialisation, on retourne une liste vide
                        return new CommandeEnrichieDto(
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

        @GetMapping("/commande/id/{id}")
        public CommandeEnrichieDto getCommandeById(@PathVariable Long id) {
            return commandeRepository.findById(id)
                .map(c -> new CommandeEnrichieDto(
                    c.getId(),
                    c.getTableNum(),
                    commandeService.jsonToItemsEnrichis(c.getItems()),
                    c.getStatus(),
                    c.getCreatedAt(),
                    c.getTotal()
                ))
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
        int tables = configRepository.findAll().stream()
                .findFirst()
                .map(RestaurantConfig::getNombreTables)
                .orElse(10);
        return Map.of("nombre_tables", tables);
    }
}