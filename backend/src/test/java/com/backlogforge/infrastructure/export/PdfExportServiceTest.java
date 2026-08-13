package com.backlogforge.infrastructure.export;

import com.backlogforge.domain.Epic;
import com.backlogforge.domain.Priority;
import com.backlogforge.domain.ProductBacklog;
import com.backlogforge.domain.Sprint;
import com.backlogforge.domain.Task;
import com.backlogforge.domain.UserStory;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfExportServiceTest {

    @Test
    @DisplayName("Deve gerar arquivo PDF estruturado sem exceções e carregável pelo PDFBox")
    void shouldGenerateValidPdfDocument() throws IOException {
        PdfExportService service = new PdfExportService();

        Task task1 = new Task("TSK-001", "Criar controllers", "Implementar endpoints REST");
        Task task2 = new Task("TSK-002", "Criar testes unitários", "Cobrir regras de negócio");

        UserStory us1 = new UserStory(
                "US-001",
                "Estruturar Projeto",
                "Como dev quero criar a estrutura inicial.",
                Priority.CRITICAL,
                5,
                List.of("Projeto deve compilar", "Testes devem passar"),
                List.of(task1, task2)
        );

        Epic epic1 = new Epic("EPIC-001", "Fundação do Sistema", "Épico base da arquitetura", List.of(us1));
        Sprint sprint1 = new Sprint("SPRINT-01", "Sprint 1", "Entregar base do backend", List.of("US-001"));

        ProductBacklog backlog = new ProductBacklog(
                "Projeto FATEC Teste PDF",
                "Resumo do backlog para validação de exportação em formato PDF.",
                List.of("Java 21", "Spring Boot", "React"),
                List.of(epic1),
                List.of(sprint1)
        );

        byte[] pdfBytes = service.generatePdf(backlog);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        // Validar que o PDF pode ser lido pelo PDFBox 3.x
        try (PDDocument doc = Loader.loadPDF(pdfBytes)) {
            assertTrue(doc.getNumberOfPages() >= 1);
        }
    }
}
