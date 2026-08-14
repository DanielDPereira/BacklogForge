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

        return new ProductBacklog(
                raw.projectName() != null ? raw.projectName() : request.projectName(),
                raw.summary(),
                raw.suggestedTechnologies(),
                raw.epics(),
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
