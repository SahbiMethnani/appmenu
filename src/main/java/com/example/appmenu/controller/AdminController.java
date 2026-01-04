package com.example.appmenu.controller;
import java.util.Comparator;
import com.example.appmenu.dto.*;
import com.example.appmenu.entity.*;
import com.example.appmenu.repository.*;
import com.example.appmenu.service.CommandeService;
import com.example.appmenu.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password-hash}")
    private String adminPasswordHash;

    private final JwtService jwtService;
    private final PlatRepository platRepository;
    private final CommandeRepository commandeRepository;
    private final CategorieImageRepository categorieImageRepository;
    private final RestaurantConfigRepository configRepository;
    private final CommandeService commandeService;

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody AdminLoginRequest request) {
        if (!adminUsername.equals(request.username()) || !BCrypt.checkpw(request.password(), adminPasswordHash)) {
            throw new RuntimeException("Identifiants incorrects");
        }

        String token = jwtService.generateToken(request.username());

        return Map.of(
                "access_token", token,
                "token_type", "bearer",
                "expires_in", 24 * 3600
        );
    }

    @GetMapping("/commandes")
    public List<CommandeEnrichieDto> getAllCommandes() {
        return commandeRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(c -> new CommandeEnrichieDto(
                        c.getId(),
                        c.getTableNum(),
                        commandeService.jsonToItemsEnrichis(c.getItems()),  // Méthode enrichie
                        c.getStatus(),
                        c.getCreatedAt(),
                        c.getTotal()
                ))
                .toList();
    }
    @PutMapping("/commande/{id}")
    public Map<String, String> updateCommandeStatus(@PathVariable Long id, @Valid @RequestBody CommandeUpdateRequest request) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        commande.setStatus(request.status());
        commandeRepository.save(commande);
        return Map.of("message", "Statut mis à jour avec succès");
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        LocalDate today = LocalDate.now();
        List<Commande> todayCommandes = commandeRepository.findAll().stream()
                .filter(c -> c.getCreatedAt().toLocalDate().equals(today))
                .toList();

        long total = todayCommandes.size();
        long enAttente = todayCommandes.stream().filter(c -> "en_attente".equals(c.getStatus())).count();
        long preparation = todayCommandes.stream().filter(c -> "preparation".equals(c.getStatus())).count();
        long prete = todayCommandes.stream().filter(c -> "prete".equals(c.getStatus())).count();
        double ca = todayCommandes.stream().mapToDouble(c -> c.getTotal() != null ? c.getTotal() : 0).sum();

        return Map.of(
                "total_commandes_today", total,
                "en_attente", enAttente,
                "preparation", preparation,
                "prete", prete,
                "chiffre_affaires_today", Math.round(ca * 100.0) / 100.0
        );
    }

    // CRUD Plats
    @PostMapping("/plat")
    public PlatDto createPlat(@Valid @RequestBody PlatDto dto) {
        Plat plat = Plat.builder()
                .nom(dto.nom()).description(dto.description()).prix(dto.prix())
                .categorie(dto.categorie()).imageUrl(dto.imageUrl()).build();
        plat = platRepository.save(plat);
        return new PlatDto(plat.getId(), plat.getNom(), plat.getDescription(), plat.getPrix(), plat.getCategorie(), plat.getImageUrl());
    }

    @PutMapping("/plat/{id}")
    public PlatDto updatePlat(@PathVariable Long id, @Valid @RequestBody PlatDto dto) {
        Plat plat = platRepository.findById(id).orElseThrow();
        plat.setNom(dto.nom());
        plat.setDescription(dto.description());
        plat.setPrix(dto.prix());
        plat.setCategorie(dto.categorie());
        plat.setImageUrl(dto.imageUrl());
        plat = platRepository.save(plat);
        return new PlatDto(plat.getId(), plat.getNom(), plat.getDescription(), plat.getPrix(), plat.getCategorie(), plat.getImageUrl());
    }

    @DeleteMapping("/plat/{id}")
    public ResponseEntity<Void> deletePlat(@PathVariable Long id) {
        platRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Images catégories
    @PostMapping("/categorie/image")
    public Map<String, String> uploadCategorieImage(@Valid @RequestBody CategorieImageDto dto) {
        CategorieImage img = categorieImageRepository.findByCategorie(dto.categorie())
                .orElse(new CategorieImage());
        img.setCategorie(dto.categorie());
        img.setImageBase64(dto.imageBase64());
        categorieImageRepository.save(img);
        return Map.of("message", "Image de la catégorie '" + dto.categorie() + "' sauvegardée");
    }

    @DeleteMapping("/categorie/image/{categorie}")
    public Map<String, String> deleteCategorieImage(@PathVariable String categorie) {
        categorieImageRepository.findByCategorie(categorie).ifPresent(categorieImageRepository::delete);
        return Map.of("message", "Image de la catégorie '" + categorie + "' supprimée");
    }

    // Config tables
    @PutMapping("/config/tables")
    public Map<String, String> updateTableConfig(@RequestBody Map<String, Integer> body) {
        int nombre = body.get("nombre_tables");
        if (nombre < 1 || nombre > 200) {
            throw new IllegalArgumentException("Nombre de tables invalide");
        }
        RestaurantConfig config = configRepository.findById(1L).orElse(new RestaurantConfig());
        config.setNombreTables(nombre);
        configRepository.save(config);
        return Map.of("message", "Nombre de tables mis à jour: " + nombre);
    }
}