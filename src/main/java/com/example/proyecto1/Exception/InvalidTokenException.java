package com.example.proyecto1.Exception;

/**
 * Excepción lanzada cuando un token JWT es inválido, malformado o ha expirado.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}