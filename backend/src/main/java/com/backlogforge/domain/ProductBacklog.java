package com.backlogforge.domain;

import java.util.List;

/**
 * Representa a estrutura completa do Product Backlog gerado.
 */
public record ProductBacklog(
        String projectName,
        String summary,
        List<String> suggestedTechnologies,
        List<Epic> epics,
        List<Sprint> sprints
) {
    public ProductBacklog {
        if (suggestedTechnologies == null) {
            suggestedTechnologies = List.of();
        }
        if (epics == null) {
            epics = List.of();
        }
        if (sprints == null) {
            sprints = List.of();
        }
    }
}
