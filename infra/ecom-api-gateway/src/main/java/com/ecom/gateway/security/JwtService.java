package com.ecom.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Collection;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));

        this.expirationMs = expirationMs;
    }

    public String generateToken(String username) {
        return generateToken(username, List.of("USER"));
    }

    public String generateToken(String username, Collection<String> roles) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {

        return extractClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Object roleClaim = extractClaims(token).get("role");
        if (roleClaim instanceof String role && !role.isBlank()) {
            return List.of(role);
        }

        // Accept tokens created by the earlier gateway implementation during rollout.
        Object claim = extractClaims(token).get("roles");
        if (claim instanceof Collection<?> values) {
            return values.stream().filter(String.class::isInstance)
                    .map(String.class::cast).toList();
        }
        if (claim instanceof String role && !role.isBlank()) {
            return List.of(role);
        }
        // Keep tokens issued before RBAC was added usable as ordinary users.
        return List.of("USER");
    }

    public boolean isTokenValid(String token) {

        try {
            Claims claims = extractClaims(token);

            return claims.getExpiration().after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
