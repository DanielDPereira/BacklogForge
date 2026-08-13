package com.backlogforge.domain;

import java.util.List;

/**
 * Representa uma User Story no Product Backlog.
 */
public record UserStory(
        String id,
        String title,
        String description,
        Priority priority,
        Integer storyPoints,
        List<String> acceptanceCriteria,
        List<Task> tasks
) {
    public UserStory {
        if (acceptanceCriteria == null) {
            acceptanceCriteria = List.of();
        }
        if (tasks == null) {
            tasks = List.of();
        }
    }
}
