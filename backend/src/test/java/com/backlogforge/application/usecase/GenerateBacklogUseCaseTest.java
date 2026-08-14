package com.backlogforge.application.usecase;

import com.backlogforge.application.ai.AiService;
import com.backlogforge.domain.Epic;
import com.backlogforge.domain.Priority;
import com.backlogforge.domain.ProductBacklog;
import com.backlogforge.domain.Sprint;
import com.backlogforge.domain.Task;
import com.backlogforge.domain.UserStory;
import com.backlogforge.domain.exception.InvalidBacklogException;
import com.backlogforge.infrastructure.ai.BacklogPromptBuilder;
import com.backlogforge.web.dto.GenerateBacklogRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class GenerateBacklogUseCaseTest {

    private AiService aiService;
    private BacklogPromptBuilder promptBuilder;
    private GenerateBacklogUseCase useCase;

    @BeforeEach
    void setUp() {
        aiService = Mockito.mock(AiService.class);
        promptBuilder = new BacklogPromptBuilder();
        useCase = new GenerateBacklogUseCase(aiService, promptBuilder);
    }

    @Test
    @DisplayName("Deve gerar backlog com sucesso quando a IA retornar estrutura válida")
    void shouldGenerateBacklogSuccessfully() {
        GenerateBacklogRequest request = new GenerateBacklogRequest(
                "Gestão Escolar",
                2,
                2,
                3,
                List.of("Java"),
                false,
                "Sistema para gestão de alunos"
        );

        ProductBacklog mockBacklog = new ProductBacklog(
                "Gestão Escolar",
                "Resumo do projeto",
                List.of("Java"),
                List.of(
                        new Epic("EPIC-001", "Cadastro de Alunos", "Épico para cadastro", List.of(
                                new UserStory("US-001", "Manter Alunos", "Como secretária...", Priority.HIGH, 5, List.of("Criar aluno"), List.of(
                                        new Task("TASK-001", "Criar controller", "Desenvolver controller", Priority.HIGH)
                                ))
                        ))
                ),
                List.of(
                        new Sprint("SPRINT-01", "Sprint 1", "Objetivo 1", List.of("US-001")),
                        new Sprint("SPRINT-02", "Sprint 2", "Objetivo 2", List.of())
                )
        );

        when(aiService.generateStructured(any(), eq(ProductBacklog.class))).thenReturn(mockBacklog);

        ProductBacklog result = useCase.execute(request, null);

        assertNotNull(result);
        assertEquals("Gestão Escolar", result.projectName());
        assertEquals(2, result.sprints().size());
        assertEquals(1, result.epics().size());
    }

    @Test
    @DisplayName("Deve normalizar a quantidade de Sprints se a IA retornar quantidade diferente do solicitado")
    void shouldNormalizeSprintCountWhenMismatchOccurs() {
        GenerateBacklogRequest request = new GenerateBacklogRequest(
                "Gestão Escolar",
                3,
                2,
                3,
                List.of(),
                false,
                null
        );

        // Retorna apenas 1 sprint quando 3 foram solicitadas
        ProductBacklog mockBacklog = new ProductBacklog(
                "Gestão Escolar",
                "Resumo",
                List.of(),
                List.of(new Epic("EPIC-001", "Épico", "Desc", List.of())),
                List.of(new Sprint("SPRINT-01", "Sprint 1", "Obj", List.of()))
        );

        when(aiService.generateStructured(any(), eq(ProductBacklog.class))).thenReturn(mockBacklog);

        ProductBacklog result = useCase.execute(request, null);

        assertNotNull(result);
        assertEquals(3, result.sprints().size());
        assertEquals("SPRINT-01", result.sprints().get(0).id());
        assertEquals("SPRINT-02", result.sprints().get(1).id());
        assertEquals("SPRINT-03", result.sprints().get(2).id());
    }
}
