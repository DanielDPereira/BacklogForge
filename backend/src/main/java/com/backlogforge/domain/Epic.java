package com.backlogforge.domain;

import java.util.List;

/**
 * Representa um Épico que agrupa User Stories relacionadas.
 */
public record Epic(
        String id,
        String title,
        String description,
        List<UserStory> userStories
) {
    public Epic {
        if (userStories == null) {
            userStories = List.of();
        }
    }
}
