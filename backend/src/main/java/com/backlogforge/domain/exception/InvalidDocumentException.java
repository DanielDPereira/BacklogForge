package com.backlogforge.domain.exception;

/**
 * Exceção lançada quando um documento PDF enviado é inválido, corrompido ou possui formato não suportado.
 */
public class InvalidDocumentException extends RuntimeException {

    public InvalidDocumentException(String message) {
        super(message);
    }

    public InvalidDocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
