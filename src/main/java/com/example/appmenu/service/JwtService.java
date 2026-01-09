package com.example.appmenu.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-hours:24}")
    private int expirationHours;

    /**
     * Génère un token JWT pour l'utilisateur
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

        long expirationMs = expirationHours * 3600 * 1000L;

        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();

        log.debug("🔑 Token JWT généré pour l'utilisateur: {}", username);
        return token;
    }

    /**
     * Extrait le username du token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait un claim spécifique du token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait tous les claims du token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'extraction des claims: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Vérifie si le token est expiré
     */
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.error("❌ Erreur lors de la vérification de l'expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Valide le token JWT
     */
    public Boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("❌ Token invalide: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Alternative : validateToken (même fonctionnalité)
     */
    public Boolean validateToken(String token) {
        return isTokenValid(token);
    }

    /**
     * Génère la clé de signature à partir du secret
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}