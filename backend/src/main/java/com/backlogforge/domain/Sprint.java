package com.backlogforge.domain;

import java.util.List;

/**
 * Representa uma Sprint do planejamento, com objetivo e IDs das User Stories alocadas.
 */
public record Sprint(
        String id,
        String name,
        String goal,
        List<String> userStoryIds
) {
    public Sprint {
        if (userStoryIds == null) {
            userStoryIds = List.of();
        }
    }
}
