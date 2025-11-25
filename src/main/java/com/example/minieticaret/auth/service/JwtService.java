package com.example.minieticaret.auth.service;

import com.example.minieticaret.auth.domain.User;
import com.example.minieticaret.common.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtService {

    private final JwtProperties properties;
    private final Key signingKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        if (properties.secret() == null || properties.secret().length() < 32) {
            throw new IllegalStateException("JWT secret en az 32 karakter olmalidir");
        }
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes());
    }

    public String generateAccessToken(User user) {
        return generateToken(user, TokenType.ACCESS, properties.accessTokenValidityMs());
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, TokenType.REFRESH, properties.refreshTokenValidityMs());
    }

    public boolean isAccessToken(String token) {
        return TokenType.ACCESS.name().equalsIgnoreCase(extractAllClaimsSafe(token).get("type", String.class));
    }

    public boolean isRefreshToken(String token) {
        return TokenType.REFRESH.name().equalsIgnoreCase(extractAllClaimsSafe(token).get("type", String.class));
    }

    public boolean isTokenValid(String token, User user) {
        String username = extractUsername(token);
        return username.equalsIgnoreCase(user.getEmail()) && !isExpired(token);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private boolean isExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private String generateToken(User user, TokenType type, long validityMs) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(validityMs);

        Map<String, Object> claims = Map.of(
                "roles", extractRoleNames(user),
                "type", type.name(),
                "uid", user.getId().toString()
        );

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuer(properties.issuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims extractAllClaimsSafe(String token) {
        try {
            return extractAllClaims(token);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Gecersiz token", ex);
        }
    }

    private Set<String> extractRoleNames(User user) {
        return user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}
