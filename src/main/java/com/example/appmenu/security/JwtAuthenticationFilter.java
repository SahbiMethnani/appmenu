/*package com.example.appmenu.security;

import com.example.appmenu.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        if (jwtService.isTokenValid(jwt)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    "admin", null, java.util.Collections.singletonList(() -> "ROLE_ADMIN"));
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}*/
package com.example.appmenu.security;

import com.example.appmenu.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    // ✅ Liste des endpoints publics (ne nécessitent pas d'authentification)
    private static final List<String> PUBLIC_PATHS = List.of(
            "/menu",
            "/health",
            "/commande",
            "/config/tables",
            "/categories/images",
            "/admin/login",
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-resources",
            "/webjars"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        log.debug("🔍 Requête entrante: {} {}", method, requestPath);

        // ✅ Skip le filtre JWT pour les endpoints publics
        if (isPublicPath(requestPath)) {
            log.debug("✅ Endpoint public, pas d'authentification requise: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Gérer les requêtes OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.debug("✅ Requête OPTIONS (CORS preflight), pas d'authentification requise");
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Récupérer le header Authorization
        final String authHeader = request.getHeader("Authorization");

        // Si pas de header Authorization, continuer sans authentification
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("⚠️ Pas de token JWT dans la requête: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // ✅ Extraire le token JWT
            final String jwt = authHeader.substring(7);
            log.debug("🔐 Token JWT détecté, validation en cours...");

            // ✅ Valider le token
            if (jwtService.isTokenValid(jwt)) {
                // ✅ Extraire le username du token
                String username = jwtService.extractUsername(jwt);

                log.debug("✅ Token valide pour l'utilisateur: {}", username);

                // ✅ Créer l'authentification si elle n'existe pas déjà
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("✅ Authentification réussie pour: {}", username);
                }
            } else {
                log.warn("❌ Token JWT invalide ou expiré");
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la validation du token JWT: {}", e.getMessage());
            // Ne pas bloquer la requête, laisser Spring Security gérer l'absence d'auth
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Vérifie si le chemin est public (ne nécessite pas d'authentification)
     */
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}