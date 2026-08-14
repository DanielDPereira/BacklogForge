package com.backlogforge.infrastructure.export;

import com.backlogforge.domain.Epic;
import com.backlogforge.domain.ProductBacklog;
import com.backlogforge.domain.Sprint;
import com.backlogforge.domain.Task;
import com.backlogforge.domain.UserStory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço responsável por exportar o Product Backlog no formato CSV universal,
 * compatível com importação em plataformas de gestão ágil como Jira, Trello e Azure DevOps.
 */
@Service
public class CsvExportService {

    private static final String CSV_HEADER = "Issue Type,Issue Id,Parent Id,Summary,Description,Priority,Story Points,Sprint,Epic Name\n";

    public byte[] generateCsv(ProductBacklog backlog) {
        if (backlog == null) {
            return new byte[0];
        }

        // Mapeamento auxiliar: UserStoryId -> Nome da Sprint alocada
        Map<String, String> storySprintMap = new HashMap<>();
        if (backlog.sprints() != null) {
            for (Sprint sprint : backlog.sprints()) {
                if (sprint.userStoryIds() != null) {
                    for (String storyId : sprint.userStoryIds()) {
                        storySprintMap.put(storyId, sprint.name());
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        
        // Adiciona BOM (Byte Order Mark) UTF-8 para garantir acentuação correta no Excel e Jira
        sb.append("\uFEFF");
        sb.append(CSV_HEADER);

        if (backlog.epics() != null) {
            for (Epic epic : backlog.epics()) {
                // 1. Linha do Épico
                appendRow(sb,
                        "Epic",
                        epic.id(),
                        "", // Sem pai
                        epic.title(),
                        epic.description(),
                        "HIGH",
                        "", // Story points
                        "", // Sprint
                        epic.title()
                );

                if (epic.userStories() != null) {
                    for (UserStory story : epic.userStories()) {
                        // Monta a descrição detalhada com Critérios de Aceitação
                        StringBuilder fullDesc = new StringBuilder();
                        if (story.description() != null && !story.description().isBlank()) {
                            fullDesc.append(story.description());
                        }
                        if (story.acceptanceCriteria() != null && !story.acceptanceCriteria().isEmpty()) {
                            if (fullDesc.length() > 0) {
                                fullDesc.append("\n\n");
                            }
                            fullDesc.append("Critérios de Aceitação:\n");
                            for (String criteria : story.acceptanceCriteria()) {
                                fullDesc.append("- ").append(criteria).append("\n");
                            }
                        }

                        String assignedSprint = storySprintMap.getOrDefault(story.id(), "");
                        String storyPointsStr = story.storyPoints() != null ? String.valueOf(story.storyPoints()) : "";

                        // 2. Linha da User Story
                        appendRow(sb,
                                "Story",
                                story.id(),
                                epic.id(), // Parent Id é o Épico
                                story.title(),
                                fullDesc.toString().trim(),
                                story.priority() != null ? story.priority().name() : "MEDIUM",
                                storyPointsStr,
                                assignedSprint,
                                epic.title()
                        );

                        // 3. Linhas das Tasks Operacionais (Sub-tasks)
                        if (story.tasks() != null) {
                            for (Task task : story.tasks()) {
                                appendRow(sb,
                                        "Sub-task",
                                        task.id(),
                                        story.id(), // Parent Id é a User Story
                                        task.title(),
                                        task.description(),
                                        task.priority() != null ? task.priority().name() : "MEDIUM",
                                        "",
                                        assignedSprint,
                                        ""
                                );
                            }
                        }
                    }
                }
            }
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void appendRow(StringBuilder sb, String issueType, String issueId, String parentId, String summary, String description, String priority, String storyPoints, String sprint, String epicName) {
        sb.append(escapeCsvField(issueType)).append(",")
                .append(escapeCsvField(issueId)).append(",")
                .append(escapeCsvField(parentId)).append(",")
                .append(escapeCsvField(summary)).append(",")
                .append(escapeCsvField(description)).append(",")
                .append(escapeCsvField(priority)).append(",")
                .append(escapeCsvField(storyPoints)).append(",")
                .append(escapeCsvField(sprint)).append(",")
                .append(escapeCsvField(epicName)).append("\n");
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        String val = field.trim();
        if (val.contains(",") || val.contains("\"") || val.contains("\n") || val.contains("\r")) {
            val = val.replace("\"", "\"\"");
            return "\"" + val + "\"";
        }
        return val;
    }
}
