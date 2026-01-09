package com.example.appmenu.controller;

import com.example.appmenu.dto.*;
import com.example.appmenu.entity.CategorieImage;
import com.example.appmenu.entity.Plat;
import com.example.appmenu.entity.RestaurantConfig;
import com.example.appmenu.repository.*;
import com.example.appmenu.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "app.admin.username=admin",
    "app.admin.password-hash=$2a$12$KWGLOlguCW/xJJ4mvJdCYeH/TfK9Uay6VaoVZj.SX8e/m7tT4RAt."  // BCrypt hash of "admin123"
})
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlatRepository platRepository;

    @Autowired
    private RestaurantConfigRepository configRepository;

    @Autowired
    private CategorieImageRepository categorieImageRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private JwtService jwtService;

    private String adminToken;

    @BeforeEach
    public void setup() {
        platRepository.deleteAll();
        configRepository.deleteAll();
        categorieImageRepository.deleteAll();
        commandeRepository.deleteAll();
        
        // Generate token for testing
        adminToken = jwtService.generateToken("admin");
    }

    @Test
    public void testAdminLoginSuccess() throws Exception {
        AdminLoginRequest request = new AdminLoginRequest("admin", "admin123");
        
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", notNullValue()))
                .andExpect(jsonPath("$.token_type", is("bearer")))
                .andExpect(jsonPath("$.expires_in", is(24 * 3600)));
    }

    @Test
    public void testAdminLoginFailure() throws Exception {
        AdminLoginRequest request = new AdminLoginRequest("admin", "wrongpassword");
        
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAllCommandes() throws Exception {
        mockMvc.perform(get("/admin/commandes")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    public void testGetStats() throws Exception {
        mockMvc.perform(get("/admin/stats")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_commandes_today", isA(Number.class)))
                .andExpect(jsonPath("$.en_attente", isA(Number.class)))
                .andExpect(jsonPath("$.preparation", isA(Number.class)))
                .andExpect(jsonPath("$.prete", isA(Number.class)))
                .andExpect(jsonPath("$.chiffre_affaires_today", isA(Number.class)));
    }

    @Test
    public void testCreatePlat() throws Exception {
        PlatDto dto = new PlatDto(null, "Pizza Margherita", "Classique tomate et mozzarella", 12.50, "Pizzas", "http://image.jpg");
        
        mockMvc.perform(post("/admin/plat")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("Pizza Margherita")))
                .andExpect(jsonPath("$.prix", is(12.50)));
    }

    @Test
    public void testUpdatePlat() throws Exception {
        // Create a plat first
        Plat plat = Plat.builder()
                .nom("Pizza Classique")
                .description("Original")
                .prix(10.00)
                .categorie("Pizzas")
                .imageUrl("http://image.jpg")
                .build();
        plat = platRepository.save(plat);

        PlatDto updateDto = new PlatDto(plat.getId(), "Pizza Premium", "Mise à jour", 15.50, "Pizzas", "http://new.jpg");
        
        mockMvc.perform(put("/admin/plat/{id}", plat.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("Pizza Premium")))
                .andExpect(jsonPath("$.prix", is(15.50)));
    }

    @Test
    public void testDeletePlat() throws Exception {
        Plat plat = Plat.builder()
                .nom("Pizza à supprimer")
                .description("Delete me")
                .prix(10.00)
                .categorie("Pizzas")
                .imageUrl("http://image.jpg")
                .build();
        plat = platRepository.save(plat);

        mockMvc.perform(delete("/admin/plat/{id}", plat.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUploadCategorieImage() throws Exception {
        CategorieImageDto dto = new CategorieImageDto("Pizzas", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");
        
        mockMvc.perform(post("/admin/categorie/image")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("sauvegardée")));
    }

    @Test
    public void testDeleteCategorieImage() throws Exception {
        // Create an image first
        CategorieImage img = new CategorieImage();
        img.setCategorie("Pizzas");
        img.setImageBase64("base64data");
        categorieImageRepository.save(img);

        mockMvc.perform(delete("/admin/categorie/image/{categorie}", "Pizzas")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("supprimée")));
    }

    @Test
    public void testUpdateTableConfig() throws Exception {
        Map<String, Integer> body = Map.of("nombre_tables", 20);
        
        mockMvc.perform(put("/admin/config/tables")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("20")));
    }

    @Test
    public void testUpdateTableConfigInvalidNumber() throws Exception {
        Map<String, Integer> body = Map.of("nombre_tables", 300);
        
        mockMvc.perform(put("/admin/config/tables")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}
