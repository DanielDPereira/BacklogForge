package com.backlogforge.infrastructure.export;

import com.backlogforge.domain.Epic;
import com.backlogforge.domain.ProductBacklog;
import com.backlogforge.domain.Sprint;
import com.backlogforge.domain.Task;
import com.backlogforge.domain.UserStory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço determinístico para conversão do Product Backlog validado em formato Markdown.
 */
@Service
public class MarkdownExportService {

    public String generateMarkdown(ProductBacklog backlog) {
        StringBuilder sb = new StringBuilder();

        // Título e Resumo
        sb.append("# Product Backlog — ").append(backlog.projectName()).append("\n\n");

        if (backlog.summary() != null && !backlog.summary().isBlank()) {
            sb.append("> ").append(backlog.summary().trim()).append("\n\n");
        }

        // Tecnologias sugeridas / utilizadas
        if (backlog.suggestedTechnologies() != null && !backlog.suggestedTechnologies().isEmpty()) {
            sb.append("## Stack Tecnológica\n");
            for (String tech : backlog.suggestedTechnologies()) {
                sb.append("- ").append(tech).append("\n");
            }
            sb.append("\n---\n\n");
        }

        // Mapeamento de User Stories por ID para exibição nas Sprints
        Map<String, UserStory> userStoryMap = backlog.epics().stream()
                .flatMap(epic -> epic.userStories().stream())
                .collect(Collectors.toMap(UserStory::id, us -> us, (us1, us2) -> us1));

        // Épicos e User Stories
        sb.append("## Visão Geral por Épicos\n\n");
        for (Epic epic : backlog.epics()) {
            sb.append("### ").append(epic.id()).append(" — ").append(epic.title()).append("\n\n");
            if (epic.description() != null && !epic.description().isBlank()) {
                sb.append(epic.description().trim()).append("\n\n");
            }

            for (UserStory us : epic.userStories()) {
                sb.append("#### ").append(us.id()).append(" — ").append(us.title()).append("\n\n");
                sb.append("**Prioridade:** ").append(us.priority()).append("  \n");
                sb.append("**Story Points:** ").append(us.storyPoints() != null ? us.storyPoints() : "-").append("\n\n");
                sb.append(us.description()).append("\n\n");

                if (us.acceptanceCriteria() != null && !us.acceptanceCriteria().isEmpty()) {
                    sb.append("**Critérios de Aceitação:**\n\n");
                    for (String criteria : us.acceptanceCriteria()) {
                        sb.append("* ").append(criteria).append("\n");
                    }
                    sb.append("\n");
                }

                if (us.tasks() != null && !us.tasks().isEmpty()) {
                    sb.append("**Tasks:**\n\n");
                    for (Task task : us.tasks()) {
                        sb.append("* [ ] **").append(task.id()).append("**: ").append(task.title());
                        if (task.description() != null && !task.description().isBlank()) {
                            sb.append(" — ").append(task.description());
                        }
                        sb.append("\n");
                    }
                    sb.append("\n");
                }

                sb.append("---\n\n");
            }
        }

        // Sprints
        if (backlog.sprints() != null && !backlog.sprints().isEmpty()) {
            sb.append("## Planejamento das Sprints\n\n");
            for (Sprint sprint : backlog.sprints()) {
                sb.append("### ").append(sprint.name()).append(" (").append(sprint.id()).append(")\n\n");
                sb.append("**Objetivo:** ").append(sprint.goal()).append("\n\n");
                sb.append("**User Stories Alocadas:**\n\n");

                if (sprint.userStoryIds() == null || sprint.userStoryIds().isEmpty()) {
                    sb.append("*Nenhuma User Story alocada para esta Sprint.*\n\n");
                } else {
                    for (String usId : sprint.userStoryIds()) {
                        UserStory us = userStoryMap.get(usId);
                        if (us != null) {
                            sb.append("* **").append(us.id()).append("**: ").append(us.title())
                              .append(" (").append(us.storyPoints()).append(" pts, ").append(us.priority()).append(")\n");
                        } else {
                            sb.append("* **").append(usId).append("**\n");
                        }
                    }
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }
}
