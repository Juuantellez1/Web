package com.example.proyecto1.Security;

import com.example.proyecto1.Dto.AuthorizedDTO;
import com.example.proyecto1.Dto.UserExtendDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey SECRET_KEY = generateSecretKey();
    private final long EXPIRATION = 1000 * 60 * 60 * 72;

    private static SecretKey generateSecretKey() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.trim().isEmpty()) {
            secret = "founders-2025-tesis-tesos-jwt-secret-key-minimum-256-bits-for-hs256-algorithm";
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(ajustarLongitudClave(keyBytes));
    }

    private static byte[] ajustarLongitudClave(byte[] keyBytes) {
        if (keyBytes.length < 32) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                return digest.digest(keyBytes);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        } else if (keyBytes.length > 32) {
            byte[] truncated = new byte[32];
            System.arraycopy(keyBytes, 0, truncated, 0, 32);
            return truncated;
        }
        return keyBytes;
    }

    public String generateToken(String subject, String role) {
        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            System.out.println("Error validando token: " + e.getMessage());
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String hashSHA1(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : hashBytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash SHA-1", e);
        }
    }

    public AuthorizedDTO getAuthorized(String token) throws JsonMappingException, JsonProcessingException {
        String jusuario = getClaims(token).getSubject();
        ObjectMapper objectMapper = new ObjectMapper();
        UserExtendDTO user = objectMapper.readValue(jusuario, UserExtendDTO.class);
        String newToken = generateToken(jusuario, "APP_USER");
        return new AuthorizedDTO(user, newToken, "Bearer");
    }
}