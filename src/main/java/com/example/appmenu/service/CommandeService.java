package com.example.appmenu.service;

import com.example.appmenu.dto.ItemCommandeDto;
import com.example.appmenu.dto.ItemCommandeEnrichiDto;
import com.example.appmenu.entity.Plat;
import com.example.appmenu.repository.PlatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final PlatRepository platRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
     *
     * @param items la liste des items à convertir
     * @return la représentation JSON
     * @throws JsonProcessingException si la sérialisation échoue
     */
    public String itemsToJson(List<ItemCommandeDto> items) throws JsonProcessingException {
        if (items == null || items.isEmpty()) {
            return "[]";
        }
        return objectMapper.writeValueAsString(items);
    }

    /**
     * Convertit le JSON stocké en base en List<ItemCommandeDto>
     * Retourne une liste vide en cas d'erreur au lieu de lancer une exception
     *
     * @param json la chaîne JSON à parser
     * @return la liste des items, ou une liste vide si le JSON est vide ou invalide
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
            System.err.println("Erreur lors du parsing JSON: " + e.getMessage());
            System.err.println("JSON problématique: " + json);
            return Collections.emptyList();
        }
    }

    /**
     * Convertit les items JSON et les enrichit avec les informations complètes des plats
     *
     * @param json la chaîne JSON à parser
     * @return la liste des items enrichis avec les infos des plats
     */
    public List<ItemCommandeEnrichiDto> jsonToItemsEnrichis(String json) {
        List<ItemCommandeDto> items = jsonToItems(json);

        return items.stream()
                .map(item -> {
                    Plat plat = platRepository.findById(item.platId()).orElse(null);
                    if (plat == null) {
                        return null;
                    }
                    return new ItemCommandeEnrichiDto(
                            item.platId(),
                            item.quantite(),
                            plat.getNom(),
                            plat.getDescription(),
                            plat.getPrix(),
                            plat.getCategorie(),
                            plat.getImageUrl()
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }
}