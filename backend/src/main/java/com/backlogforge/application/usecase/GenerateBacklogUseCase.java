package com.backlogforge.application.usecase;

import com.backlogforge.application.ai.AiService;
import com.backlogforge.domain.ProductBacklog;
import com.backlogforge.domain.exception.AiProviderException;
import com.backlogforge.domain.exception.InvalidBacklogException;
import com.backlogforge.infrastructure.ai.BacklogPromptBuilder;
import com.backlogforge.web.dto.GenerateBacklogRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso para orquestração da geração do Product Backlog com IA.
 */
@Service
public class GenerateBacklogUseCase {

    private static final Logger log = LoggerFactory.getLogger(GenerateBacklogUseCase.class);

    private final AiService aiService;
    private final BacklogPromptBuilder promptBuilder;

    public GenerateBacklogUseCase(AiService aiService, BacklogPromptBuilder promptBuilder) {
        this.aiService = aiService;
        this.promptBuilder = promptBuilder;
    }

    public ProductBacklog execute(GenerateBacklogRequest request, String pdfText) {
        log.info("Iniciando geração de Product Backlog para o projeto '{}' (Sprints: {}, Time: {})",
                request.projectName(), request.sprintCount(), request.teamSize());

        String prompt = promptBuilder.buildPrompt(request, pdfText);
        ProductBacklog rawBacklog = aiService.generateStructured(prompt, ProductBacklog.class);

        ProductBacklog backlog = normalizeBacklog(rawBacklog, request);
        validateGeneratedBacklog(backlog, request);

        log.info("Product Backlog gerado e validado com sucesso para o projeto '{}'. Epics: {}, Sprints: {}",
                request.projectName(), backlog.epics().size(), backlog.sprints().size());

        return backlog;
    }

    private ProductBacklog normalizeBacklog(ProductBacklog raw, GenerateBacklogRequest request) {
        if (raw == null) return null;

        List<com.backlogforge.domain.Sprint> currentSprints = raw.sprints() != null ? new java.util.ArrayList<>(raw.sprints()) : new java.util.ArrayList<>();
        int requestedCount = request.sprintCount();

        if (currentSprints.size() < requestedCount) {
            log.warn("A IA gerou {} Sprints para um pedido de {} Sprints. Ajustando automaticamente...", currentSprints.size(), requestedCount);
            while (currentSprints.size() < requestedCount) {
                int nextNum = currentSprints.size() + 1;
                String sprintId = String.format("SPRINT-%02d", nextNum);
                String sprintName = "Sprint " + nextNum;
                String goal = "Refinamento, testes de integração e entregas incrementais da Sprint " + nextNum;
                currentSprints.add(new com.backlogforge.domain.Sprint(sprintId, sprintName, goal, java.util.List.of()));
            }
        } else if (currentSprints.size() > requestedCount) {
            log.warn("A IA gerou {} Sprints para um pedido de {} Sprints. Truncando para as primeiras {} Sprints...", currentSprints.size(), requestedCount, requestedCount);
            currentSprints = new java.util.ArrayList<>(currentSprints.subList(0, requestedCount));
        }

        List<com.backlogforge.domain.Epic> normalizedEpics = new ArrayList<>();
        if (raw.epics() != null) {
            for (com.backlogforge.domain.Epic epic : raw.epics()) {
                List<com.backlogforge.domain.UserStory> normalizedStories = new ArrayList<>();
                if (epic.userStories() != null) {
                    for (com.backlogforge.domain.UserStory us : epic.userStories()) {
                        List<com.backlogforge.domain.Task> normalizedTasks = new ArrayList<>();
                        if (us.tasks() != null) {
                            for (com.backlogforge.domain.Task task : us.tasks()) {
                                String title = (task.title() != null && !task.title().isBlank())
                                        ? task.title()
                                        : "Desenvolvimento técnico do requisito " + us.id();
                                String description = (task.description() != null && !task.description().isBlank())
                                        ? task.description()
                                        : "Execução operacional, implementação e testes funcionais para: " + title;

                                normalizedTasks.add(new com.backlogforge.domain.Task(
                                        task.id(),
                                        title,
                                        description,
                                        task.priority() != null ? task.priority() : com.backlogforge.domain.Priority.MEDIUM
                                ));
                            }
                        }
                        normalizedStories.add(new com.backlogforge.domain.UserStory(
                                us.id(),
                                us.title(),
                                us.description(),
                                us.priority(),
                                us.storyPoints(),
                                us.acceptanceCriteria(),
                                normalizedTasks
                        ));
                    }
                }
                normalizedEpics.add(new com.backlogforge.domain.Epic(
                        epic.id(),
                        epic.title(),
                        epic.description(),
                        normalizedStories
                ));
            }
        }

        return new ProductBacklog(
                raw.projectName() != null ? raw.projectName() : request.projectName(),
                raw.summary(),
                raw.suggestedTechnologies(),
                normalizedEpics,
                currentSprints
        );
    }

    private void validateGeneratedBacklog(ProductBacklog backlog, GenerateBacklogRequest request) {
        if (backlog == null) {
            throw new InvalidBacklogException("O backlog gerado é nulo.");
        }

        if (backlog.sprints() == null || backlog.sprints().size() != request.sprintCount()) {
            int found = backlog.sprints() != null ? backlog.sprints().size() : 0;
            throw new InvalidBacklogException("O número de Sprints geradas (%d) difere do parâmetro solicitado (%d)."
                    .formatted(found, request.sprintCount()));
        }

        if (backlog.epics() == null || backlog.epics().isEmpty()) {
            throw new InvalidBacklogException("O backlog gerado não contém Épicos.");
        }
    }
}
