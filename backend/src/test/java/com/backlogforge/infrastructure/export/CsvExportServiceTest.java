package com.backlogforge.infrastructure.export;

import com.backlogforge.domain.Epic;
import com.backlogforge.domain.Priority;
import com.backlogforge.domain.ProductBacklog;
import com.backlogforge.domain.Sprint;
import com.backlogforge.domain.Task;
import com.backlogforge.domain.UserStory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvExportServiceTest {

    private final CsvExportService csvExportService = new CsvExportService();

    @Test
    @DisplayName("Deve gerar arquivo CSV no formato Jira/Trello com BOM UTF-8 e vinculos de parent e sprint")
    void shouldGenerateValidCsvForJiraImport() {
        Task task1 = new Task("TASK-001", "Implementar Login Backend", "Criar rotas de auth", Priority.HIGH);
        UserStory us1 = new UserStory(
                "US-001",
                "Autenticação de Usuário",
                "Como usuário quero logar no sistema",
                Priority.HIGH,
                5,
                List.of("Deve aceitar e-mail válido", "Deve retornar token JWT"),
                List.of(task1)
        );

        Epic epic1 = new Epic("EPIC-001", "Módulo de Segurança", "Épico para autenticação e perfil", List.of(us1));
        Sprint sprint1 = new Sprint("SPRINT-01", "Sprint 1", "Implementar Auth", List.of("US-001"));

        ProductBacklog backlog = new ProductBacklog(
                "Portal Educacional",
                "Resumo do portal",
                List.of("Java 21", "React"),
                List.of(epic1),
                List.of(sprint1)
        );

        byte[] csvBytes = csvExportService.generateCsv(backlog);
        assertNotNull(csvBytes);
        assertTrue(csvBytes.length > 0);

        String csvContent = new String(csvBytes, StandardCharsets.UTF_8);

        // Verifica o BOM UTF-8 (\uFEFF)
        assertTrue(csvContent.startsWith("\uFEFFIssue Type,Issue Id,Parent Id"));

        // Verifica presenças essenciais de linhas
        assertTrue(csvContent.contains("Epic,EPIC-001,,Módulo de Segurança"));
        assertTrue(csvContent.contains("Story,US-001,EPIC-001,Autenticação de Usuário"));
        assertTrue(csvContent.contains("Sub-task,TASK-001,US-001,Implementar Login Backend"));

        // Verifica mapeamento da Sprint 1 na Story US-001
        assertTrue(csvContent.contains("Sprint 1"));

        // Verifica formatação dos Critérios de Aceitação na Descrição
        assertTrue(csvContent.contains("Critérios de Aceitação:\n- Deve aceitar e-mail válido"));
    }

    @Test
    @DisplayName("Deve retornar array vazio ao receber backlog nulo")
    void shouldReturnEmptyArrayWhenBacklogIsNull() {
        byte[] bytes = csvExportService.generateCsv(null);
        assertNotNull(bytes);
        assertEquals(0, bytes.length);
    }
}
