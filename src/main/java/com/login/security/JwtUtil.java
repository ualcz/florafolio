package com.login.security;

import com.login.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.Set;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;
    
    // In a production environment, this should be stored in Redis or a database
    private Set<String> revokedTokens = new HashSet<>();

    /**
     * Revokes a token by adding it to the revoked tokens set
     * @param token The token to revoke
     */
    public void revokeToken(String token) {
        revokedTokens.add(token);
    }

    /**
     * Revokes all tokens for a specific user
     * @param userId The user ID whose tokens should be revoked
     */
    public void revokeAllUserTokens(UUID userId) {
        // In a real implementation with Redis or a database, you would delete all tokens for this user
        // For this in-memory implementation, we can't easily do this without storing user-to-token mappings
        // This is a placeholder for the concept
    }

    /**
     * Checks if a token has been revoked
     * @param token The token to check
     * @return true if the token is revoked, false otherwise
     */
    public boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }

    /**
     * Generates a JWT token for a user
     * @param user The user for whom to generate the token
     * @return The JWT token
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("role", "USER");
        return createToken(claims, user.getUsername());
    }

    /**
     * Creates a token with the specified claims and subject
     * @param claims The claims to include in the token
     * @param subject The subject of the token (usually the username)
     * @return The JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a token
     * @param token The token to validate
     * @param userDetails The user details to validate against
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !isTokenRevoked(token));
    }

    /**
     * Extracts the username from a token
     * @param token The token from which to extract the username
     * @return The username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the user ID from a token
     * @param token The token from which to extract the user ID
     * @return The user ID as a UUID
     */
    public UUID extractUserId(String token) {
        String userIdStr = extractClaim(token, claims -> claims.get("userId", String.class));
        return UUID.fromString(userIdStr);
    }

    /**
     * Extracts the expiration date from a token
     * @param token The token from which to extract the expiration date
     * @return The expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a claim from a token
     * @param token The token from which to extract the claim
     * @param claimsResolver The function to resolve the claim
     * @return The claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a token
     * @param token The token from which to extract the claims
     * @return The claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Gets the signing key for JWT tokens
     * @return The signing key
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Checks if a token is expired
     * @param token The token to check
     * @return true if the token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}