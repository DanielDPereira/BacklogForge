package com.backlogforge.domain;

/**
 * Representa uma tarefa técnica ou operacional pertencente a uma User Story.
 * Nota: As tarefas não possuem atribuição individual a pessoas da equipe.
 */
public record Task(
        String id,
        String title,
        String description,
        Priority priority
) {
}
