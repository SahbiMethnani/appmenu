package com.example.appmenu.bdd.steps;

import com.example.appmenu.dto.ItemCommandeDto;
import com.example.appmenu.entity.Commande;
import com.example.appmenu.entity.Plat;
import com.example.appmenu.repository.CommandeRepository;
import com.example.appmenu.repository.PlatRepository;
import com.example.appmenu.service.CommandeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommandeSteps {

    @Autowired
    private PlatRepository platRepository;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private CommandeRepository commandeRepository;

    private Commande commande;
    private List<ItemCommandeDto> itemsCommande;

    @Given("le menu contient des plats")
    public void le_menu_contient_des_plats() {
        List<Plat> plats = platRepository.findAll();
        if (plats.isEmpty()) {
            Plat plat1 = platRepository.save(new Plat(null, "Pizza Margherita", "Pizza tomate et mozzarella", 8.5, "Pizza", null));
            Plat plat2 = platRepository.save(new Plat(null, "Pâtes Carbonara", "Pâtes à la sauce carbonara", 9.5, "Pâtes", null));
            plats = List.of(plat1, plat2);
        }
        assertFalse(plats.isEmpty(), "Le menu doit contenir au moins un plat");
    }

    @When("le client passe une commande pour la table {int}")
    public void le_client_passe_une_commande_pour_la_table(Integer tableNum) throws JsonProcessingException {
        List<Plat> plats = platRepository.findAll();
        itemsCommande = List.of(
                new ItemCommandeDto(plats.get(0).getId(), 1),
                new ItemCommandeDto(plats.get(1).getId(), 2)
        );

        String itemsJson = commandeService.itemsToJson(itemsCommande);
        Double total = commandeService.calculateTotal(itemsCommande);
        LocalDateTime now = LocalDateTime.now();

        // Création de la commande avec le constructeur complet
        commande = new Commande(null, tableNum, itemsJson, "en_attente", now, total);
        commandeRepository.save(commande);
    }

    @Then("la commande est créée avec le statut {string}")
    public void la_commande_est_créée_avec_le_statut(String statutAttendu) {
        assertNotNull(commande, "La commande ne doit pas être nulle");
        assertEquals(statutAttendu, commande.getStatus(), "Le statut de la commande est incorrect");

        List<ItemCommandeDto> itemsRecuperes = commandeService.jsonToItems(commande.getItems());
        assertEquals(itemsCommande.size(), itemsRecuperes.size(), "Le nombre d'items dans la commande est incorrect");
    }
}
