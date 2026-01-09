package com.example.appmenu.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    public void testGenerateToken() {
        String token = jwtService.generateToken("admin");
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void testGenerateTokenAndExtractUsername() {
        String username = "admin";
        String token = jwtService.generateToken(username);
        
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    public void testGenerateTokenAndCheckExpiration() {
        String token = jwtService.generateToken("admin");
        Date expiration = jwtService.extractExpiration(token);
        
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    public void testIsTokenValidWithValidToken() {
        String token = jwtService.generateToken("admin");
        boolean isValid = jwtService.isTokenValid(token);
        
        assertTrue(isValid);
    }

    @Test
    public void testExtractClaimRole() {
        String token = jwtService.generateToken("admin");
        String role = jwtService.extractClaim(token, claims -> (String) claims.get("role"));
        
        assertEquals("ADMIN", role);
    }

    @Test
    public void testMultipleTokensAreDifferent() throws InterruptedException {
        String token1 = jwtService.generateToken("admin");
        Thread.sleep(1100);  // Wait more than 1 second to ensure different timestamps (tokens are based on seconds)
        String token2 = jwtService.generateToken("admin");
        
        // Tokens should be different (different timestamps)
        assertNotEquals(token1, token2);
        
        // But both should be valid
        assertTrue(jwtService.isTokenValid(token1));
        assertTrue(jwtService.isTokenValid(token2));
    }
}
