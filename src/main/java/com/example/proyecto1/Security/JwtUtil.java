package com.example.proyecto1.Security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.proyecto1.Dto.AuthorizedDTO;
import com.example.proyecto1.Dto.UsuarioDto;
import com.example.proyecto1.Dto.UserExtendDTO;
import com.example.proyecto1.Exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;

@Component
public class JwtUtil {
    private final SecretKey SECRET_KEY = generateSecretKey();
    private final long EXPIRATION = 1000 * 60 * 60 * 72;

    private static SecretKey generateSecretKey() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.trim().isEmpty()) {
            secret = "proyecto1-2025-jwt-secret-key-minimum-256-bits-for-hs256-algorithm";
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                keyBytes = digest.digest(keyBytes);
            } catch (NoSuchAlgorithmException e) {
                StringBuilder sb = new StringBuilder(secret);
                while (sb.length() < 32) {
                    sb.append(secret);
                }
                keyBytes = sb.substring(0, 32).getBytes(StandardCharsets.UTF_8);
            }
        } else if (keyBytes.length > 32) {
            byte[] truncated = new byte[32];
            System.arraycopy(keyBytes, 0, truncated, 0, 32);
            keyBytes = truncated;
        }

        return new SecretKeySpec(keyBytes, SIG.HS256.key().build().getAlgorithm());
    }


    public String generateToken(String jusuario, String role) {
        return Jwts.builder()
                .subject(jusuario)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }

    public AuthorizedDTO renewToken(Authentication authentication) throws JsonMappingException, JsonProcessingException {

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Authentication inválido");
        }

        String token = authentication.getCredentials().toString();
        if (!validateToken(token)) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }


        return getAuthorized(token);
    }


    public AuthorizedDTO appAuthorized(String token) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, JsonMappingException, JsonProcessingException{
        String skey = System.getenv("JWT_SECRET_APP");
        SecretKey SECRET_KEY_APP = generateKeyFromText(skey);
        String susuario = decrypt(token, SECRET_KEY_APP);
        ObjectMapper objectMapper = new ObjectMapper();
        UserExtendDTO user = objectMapper.readValue(susuario, UserExtendDTO.class);

        String newToken = generateToken(susuario, "APP_USER");
        return new AuthorizedDTO(user, newToken, "Bearer");
    }
    private SecretKey generateKeyFromText(String text) {
        byte[] keyBytes = text.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                keyBytes = digest.digest(keyBytes);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Error al generar la clave", e);
            }
        } else if (keyBytes.length > 32) {
            byte[] truncated = new byte[32];
            System.arraycopy(keyBytes, 0, truncated, 0, 32);
            keyBytes = truncated;
        }

        return new SecretKeySpec(keyBytes, "AES");
    }

    public UsuarioDto getUser(Authentication authentication) throws JsonMappingException, JsonProcessingException, InvalidTokenException {

        String token = authentication.getCredentials().toString();
        if (!validateToken(token)) {
            throw new InvalidTokenException("Token inválido o expirado");
        }

        return getAuthorized(token).getUser();
    }
    public AuthorizedDTO getAuthorized( String token ) throws JsonMappingException, JsonProcessingException {


        String jusuario = getClaims(token).getSubject();
        ObjectMapper objectMapper = new ObjectMapper();
        UserExtendDTO user = objectMapper.readValue(jusuario, UserExtendDTO.class);
        String newToken = generateToken(jusuario, "APP_USER");

        return new AuthorizedDTO(user, newToken, "Bearer");
    }

    public String hashMD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash MD5", e);
        }
    }

    public String hashSHA1(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash SHA-1", e);
        }
    }

    public String hashSHA256(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash SHA-256", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }




    public SecretKey generateKey() {
        String myKey = "PapaMissYouEveryDay";
        byte[] keyBytes = myKey.getBytes(StandardCharsets.UTF_8);

        byte[] adjustedKey = new byte[16];
        System.arraycopy(keyBytes, 0, adjustedKey, 0, Math.min(keyBytes.length, 16));

        return new SecretKeySpec(adjustedKey, "AES");
    }

    public String encrypt(String text, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

        return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedText);
    }

    public String decrypt(String encryptedText, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decodedBytes = Base64.getUrlDecoder().decode(encryptedText);

        byte[] decryptedText = cipher.doFinal(decodedBytes);
        return new String(decryptedText, StandardCharsets.UTF_8);
    }
}