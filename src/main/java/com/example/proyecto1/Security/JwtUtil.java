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
    private final long EXPIRATION = 1000 * 60 * 60 * 72; // 72 horas

    private static SecretKey generateSecretKey() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.trim().isEmpty()) {
            secret = "proyecto1-2025-jwt-secret-key-minimum-256-bits-for-hs256-algorithm";
        }

        // Asegurar que la clave tenga al menos 256 bits (32 bytes)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // Si es menor a 32 bytes, usar SHA-256 para generar una clave de 32 bytes
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                keyBytes = digest.digest(keyBytes);
            } catch (NoSuchAlgorithmException e) {
                // Fallback: repetir la clave hasta tener 32 bytes
                StringBuilder sb = new StringBuilder(secret);
                while (sb.length() < 32) {
                    sb.append(secret);
                }
                keyBytes = sb.substring(0, 32).getBytes(StandardCharsets.UTF_8);
            }
        } else if (keyBytes.length > 32) {
            // Si es mayor a 32 bytes, truncar a 32 bytes
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

    /**
     * Recibe un token JWT, lo valida y genera uno nuevo con los mismos datos.
     */
    /**
     * Recibe Authentication, extrae el token JWT del principal, lo valida y genera uno nuevo.
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
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
        // Asegúrate de que el texto tenga al menos 256 bits (32 bytes)
        byte[] keyBytes = text.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // Si es menor a 32 bytes, usa SHA-256 para generar una clave de 32 bytes
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                keyBytes = digest.digest(keyBytes);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Error al generar la clave", e);
            }
        } else if (keyBytes.length > 32) {
            // Si es mayor a 32 bytes, trunca a 32 bytes
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
        // String role = (String) getClaims(token).get("role");
        ObjectMapper objectMapper = new ObjectMapper();
        UserExtendDTO user = objectMapper.readValue(jusuario, UserExtendDTO.class);
        String newToken = generateToken(jusuario, "APP_USER");

        return new AuthorizedDTO(user, newToken, "Bearer");
    }
    /**
     * Genera un hash MD5 de los datos proporcionados
     * ⚠️ ADVERTENCIA: MD5 se considera inseguro para la mayoría de los casos de uso
     * Se recomienda usar algoritmos más seguros como SHA-256 o bcrypt para contraseñas
     *
     * @param data Los datos a hashear
     * @return Hash MD5 en formato hexadecimal
     */
    public String hashMD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash MD5", e);
        }
    }

    /**
     * Genera un hash SHA-1 de los datos proporcionados
     * ⚠️ ADVERTENCIA: SHA-1 se considera inseguro para la mayoría de los casos de uso
     * Se recomienda usar algoritmos más seguros como SHA-256 o bcrypt para contraseñas
     *
     * @param data Los datos a hashear
     * @return Hash SHA-1 en formato hexadecimal
     */
    public String hashSHA1(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash SHA-1", e);
        }
    }

    /**
     * Genera un hash SHA-256 de los datos proporcionados (recomendado)
     *
     * @param data Los datos a hashear
     * @return Hash SHA-256 en formato hexadecimal
     */
    public String hashSHA256(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash SHA-256", e);
        }
    }

    /**
     * Convierte un array de bytes a su representación hexadecimal
     *
     * @param bytes Array de bytes
     * @return String en formato hexadecimal
     */
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
        String myKey = "PapaMissYouEveryDay"; // Cambia esto por tu clave
        // Must be exactly 16 bytes (128 bits), 24 bytes (192 bits), or 32 bytes (256 bits)
        byte[] keyBytes = myKey.getBytes(StandardCharsets.UTF_8);

        // If the key is shorter, pad it; if longer, truncate it
        byte[] adjustedKey = new byte[16]; // AES-128
        System.arraycopy(keyBytes, 0, adjustedKey, 0, Math.min(keyBytes.length, 16));

        return new SecretKeySpec(adjustedKey, "AES");
    }

    // Encrypt text
    public String encrypt(String text, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

        // codificación URL-safe (sin +, /, =)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedText);
    }

    // Decrypt text
    public String decrypt(String encryptedText, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Decodificar primero desde Base64 URL-safe
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encryptedText);

        byte[] decryptedText = cipher.doFinal(decodedBytes);
        return new String(decryptedText, StandardCharsets.UTF_8);
    }
}