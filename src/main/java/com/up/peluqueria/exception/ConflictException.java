package com.up.peluqueria.exception;

/**
 * Se lanza cuando la operación entra en conflicto con el estado actual del
 * recurso (por ejemplo, eliminar un peluquero que tiene turnos asociados sin
 * confirmar la eliminación en cascada).
 * Equivale a CustomError.conflict(...) de la aplicación Node original.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
