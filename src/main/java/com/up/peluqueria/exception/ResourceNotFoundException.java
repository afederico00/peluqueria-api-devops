package com.up.peluqueria.exception;

/**
 * Se lanza cuando el recurso solicitado (peluquero o turno) no existe.
 * Equivale a CustomError.notFound(...) de la aplicación Node original.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
