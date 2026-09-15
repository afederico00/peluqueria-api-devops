package com.up.peluqueria.exception;

/**
 * Se lanza ante datos inválidos o violaciones de reglas de negocio
 * (fecha/hora inválida, nombre duplicado, turno ya reservado, etc.).
 * Equivale a CustomError.badRequest(...) de la aplicación Node original.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
