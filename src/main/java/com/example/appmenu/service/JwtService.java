package com.example.appmenu.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-hours}")
    private long expirationHours;

    /**
     * Génère un token JWT pour un utilisateur admin
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .claim("role", "admin")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationHours * 3600000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    /**
     * Extrait les claims (données) du token JWT
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrait le username (subject) du token
     */
    public String extractUsername(String token) {
        try {
            return extractClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Vérifie si le token est valide pour un utilisateur donné
     */
    public boolean isTokenValid(String token, String username) {
        try {
            Claims claims = extractClaims(token);
            return claims.getSubject().equals(username) &&
                    claims.get("role").equals("admin") &&
                    !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Vérifie si le token est valide (sans vérifier le username)
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.get("role").equals("admin") &&
                    !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}