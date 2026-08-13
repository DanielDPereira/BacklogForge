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
        ProductBacklog backlog = aiService.generateStructured(prompt, ProductBacklog.class);

        validateGeneratedBacklog(backlog, request);

        log.info("Product Backlog gerado e validado com sucesso para o projeto '{}'. Epics: {}, Sprints: {}",
                request.projectName(), backlog.epics().size(), backlog.sprints().size());

        return backlog;
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
