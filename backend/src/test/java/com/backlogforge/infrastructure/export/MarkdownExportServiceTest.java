package com.backlogforge.infrastructure.export;

import com.backlogforge.domain.Epic;
import com.backlogforge.domain.Priority;
import com.backlogforge.domain.ProductBacklog;
import com.backlogforge.domain.Sprint;
import com.backlogforge.domain.Task;
import com.backlogforge.domain.UserStory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownExportServiceTest {

    private final MarkdownExportService service = new MarkdownExportService();

    @Test
    @DisplayName("Deve gerar o arquivo Markdown formatado corretamente a partir do ProductBacklog")
    void shouldGenerateFormattedMarkdown() {
        ProductBacklog backlog = new ProductBacklog(
                "Portal Escolar",
                "Sistema de gestão escolar para alunos e professores",
                List.of("Java 21", "Spring Boot", "React"),
                List.of(
                        new Epic("EPIC-001", "Autenticação", "Módulo de login e segurança", List.of(
                                new UserStory(
                                        "US-001",
                                        "Efetuar Login",
                                        "Como usuário, quero me autenticar para acessar o sistema.",
                                        Priority.CRITICAL,
                                        3,
                                        List.of("Deve validar credenciais no banco", "Deve retornar token JWT"),
                                        List.of(
                                                new Task("TASK-001", "Criar endpoint de auth", "Implementar controller REST", Priority.CRITICAL)
                                        )
                                )
                        ))
                ),
                List.of(
                        new Sprint("SPRINT-01", "Sprint 1", "Entregar autenticação básica", List.of("US-001"))
                )
        );

        String markdown = service.generateMarkdown(backlog);

        assertNotNull(markdown);
        assertTrue(markdown.contains("# Product Backlog — Portal Escolar"));
        assertTrue(markdown.contains("EPIC-001 — Autenticação"));
        assertTrue(markdown.contains("US-001 — Efetuar Login"));
        assertTrue(markdown.contains("TASK-001"));
        assertTrue(markdown.contains("SPRINT-01"));
        assertTrue(markdown.contains("Critérios de Aceitação:"));
    }
}
