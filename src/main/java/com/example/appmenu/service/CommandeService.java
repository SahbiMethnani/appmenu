package com.example.appmenu.service;

import com.example.appmenu.dto.ItemCommandeDto;
import com.example.appmenu.entity.Plat;
import com.example.appmenu.repository.PlatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor  // Lombok génère le constructeur avec PlatRepository
public class CommandeService {

    private final PlatRepository platRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();  // Pour JSON

    /**
     * Calcule le total de la commande à partir des items
     */
    public double calculateTotal(List<ItemCommandeDto> items) {
        double total = 0.0;
        for (ItemCommandeDto item : items) {
            Plat plat = platRepository.findById(item.platId())
                    .orElseThrow(() -> new IllegalArgumentException("Plat non trouvé : ID = " + item.platId()));
            total += plat.getPrix() * item.quantite();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Convertit une liste d'ItemCommandeDto en String JSON pour stockage en base
     * Gère l'exception JsonProcessingException en interne
     *
     * @param items la liste des items à convertir
     * @return la représentation JSON
     * @throws RuntimeException si la sérialisation échoue
     */
    public String itemsToJson(List<ItemCommandeDto> items) {
        if (items == null || items.isEmpty()) {
            return "[]";
        }

        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            // Log l'erreur pour le debug
            System.err.println("Erreur lors de la sérialisation JSON: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la sérialisation des items de la commande: " + e.getMessage(), e);
        }
    }

    /**
     * Convertit le JSON stocké en base en List<ItemCommandeDto>
     * Gère l'exception JsonProcessingException en interne
     *
     * @param json la chaîne JSON à parser
     * @return la liste des items, ou une liste vide si le JSON est vide
     * @throws RuntimeException si le parsing échoue
     */
    public List<ItemCommandeDto> jsonToItems(String json) {
        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, ItemCommandeDto.class));
        } catch (JsonProcessingException e) {
            // Log l'erreur pour le debug
            System.err.println("Erreur lors du parsing JSON: " + e.getMessage());
            throw new RuntimeException("Erreur lors du parsing des items de la commande: " + e.getMessage(), e);
        }
    }
}