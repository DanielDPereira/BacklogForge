package com.backlogforge.domain.exception;

/**
 * Exceção lançada quando o Product Backlog gerado viola parâmetros determinísticos ou validações de integridade.
 */
public class InvalidBacklogException extends RuntimeException {

    public InvalidBacklogException(String message) {
        super(message);
    }
}
