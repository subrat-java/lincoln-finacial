package com.example.Api_Gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final SecretKey key;

    // Paths that don't require a token
    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/",
            "/actuator/",
            "/eureka/"
    );

    public JwtAuthFilter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Public paths pass through unchanged
        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Extract Authorization: Bearer <token>
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            reject(response, "Missing or malformed Authorization header");
            return;
        }
        String token = header.substring(7).trim();

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Object userId = claims.get("userId");
            Object roles = claims.get("roles");

            String userIdStr  = (userId == null) ? null : userId.toString();
            String rolesStr   = (roles  == null) ? null
                    : roles.toString().replace("[", "").replace("]", "").replace(" ", "");

            HttpServletRequestWrapper wrapped = new HttpServletRequestWrapper(request) {

                @Override
                public String getHeader(String name) {
                    if ("X-User-Id".equalsIgnoreCase(name) && userIdStr != null) return userIdStr;
                    if ("X-User-Roles".equalsIgnoreCase(name) && rolesStr != null) return rolesStr;
                    return super.getHeader(name);
                }

                @Override
                public Enumeration<String> getHeaders(String name) {
                    if ("X-User-Id".equalsIgnoreCase(name) && userIdStr != null) {
                        return Collections.enumeration(List.of(userIdStr));
                    }
                    if ("X-User-Roles".equalsIgnoreCase(name) && rolesStr != null) {
                        return Collections.enumeration(List.of(rolesStr));
                    }
                    return super.getHeaders(name);
                }

                @Override
                public Enumeration<String> getHeaderNames() {
                    Set<String> names = new LinkedHashSet<>();
                    Enumeration<String> original = super.getHeaderNames();
                    while (original.hasMoreElements()) {
                        names.add(original.nextElement());
                    }
                    if (userIdStr != null) names.add("X-User-Id");
                    if (rolesStr != null)  names.add("X-User-Roles");
                    return Collections.enumeration(names);
                }
            };

            chain.doFilter(wrapped, response);

        } catch (JwtException | IllegalArgumentException ex) {
            reject(response, "Invalid or expired token: " + ex.getMessage());
        }
    }

    private boolean isPublic(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}"
        );
    }
}