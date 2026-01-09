package com.example.appmenu.service;

import com.example.appmenu.dto.ItemCommandeDto;
import com.example.appmenu.entity.Plat;
import com.example.appmenu.repository.PlatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CommandeServiceTest {

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private PlatRepository platRepository;

    @BeforeEach
    public void setup() {
        platRepository.deleteAll();
    }

    @Test
    public void testCalculateTotalSingleItem() {
        Plat plat = Plat.builder()
                .nom("Pizza")
                .description("Delicious")
                .prix(12.50)
                .categorie("Pizzas")
                .imageUrl("http://image.jpg")
                .build();
        plat = platRepository.save(plat);

        ItemCommandeDto item = new ItemCommandeDto(plat.getId(), 2);
        double total = commandeService.calculateTotal(List.of(item));

        assertEquals(25.0, total);
    }

    @Test
    public void testCalculateTotalMultipleItems() {
        Plat plat1 = Plat.builder()
                .nom("Pizza")
                .description("Delicious")
                .prix(12.50)
                .categorie("Pizzas")
                .imageUrl("http://image.jpg")
                .build();
        plat1 = platRepository.save(plat1);

        Plat plat2 = Plat.builder()
                .nom("Burger")
                .description("Tasty")
                .prix(10.00)
                .categorie("Burgers")
                .imageUrl("http://burger.jpg")
                .build();
        plat2 = platRepository.save(plat2);

        ItemCommandeDto item1 = new ItemCommandeDto(plat1.getId(), 2);
        ItemCommandeDto item2 = new ItemCommandeDto(plat2.getId(), 1);

        double total = commandeService.calculateTotal(List.of(item1, item2));

        assertEquals(35.0, total);
    }

    @Test
    public void testCalculateTotalInvalidPlat() {
        ItemCommandeDto item = new ItemCommandeDto(999L, 1);
        assertThrows(IllegalArgumentException.class, () -> commandeService.calculateTotal(List.of(item)));
    }

    @Test
    public void testItemsToJsonEmpty() throws JsonProcessingException {
        String json = commandeService.itemsToJson(List.of());
        assertEquals("[]", json);
    }

    @Test
    public void testItemsToJsonNull() throws JsonProcessingException {
        String json = commandeService.itemsToJson(null);
        assertEquals("[]", json);
    }

    @Test
    public void testJsonToItemsEmpty() {
        List<ItemCommandeDto> items = commandeService.jsonToItems("[]");
        assertEquals(0, items.size());
    }

    @Test
    public void testJsonToItemsEnrichisEmpty() {
        var enriched = commandeService.jsonToItemsEnrichis("[]");
        assertEquals(0, enriched.size());
    }
}
