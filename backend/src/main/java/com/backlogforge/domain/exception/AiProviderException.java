package com.backlogforge.domain.exception;

/**
 * Exceção lançada quando ocorre erro de comunicação, quota ou indisponibilidade no provedor de IA.
 */
public class AiProviderException extends RuntimeException {

    public AiProviderException(String message) {
        super(message);
    }

    public AiProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
