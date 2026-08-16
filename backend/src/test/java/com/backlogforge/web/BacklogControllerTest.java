package com.backlogforge.web;

import com.backlogforge.application.usecase.GenerateBacklogUseCase;
import com.backlogforge.domain.Epic;
import com.backlogforge.domain.Priority;
import com.backlogforge.domain.ProductBacklog;
import com.backlogforge.domain.Sprint;
import com.backlogforge.domain.Task;
import com.backlogforge.domain.UserStory;
import com.backlogforge.infrastructure.export.CsvExportService;
import com.backlogforge.infrastructure.export.MarkdownExportService;
import com.backlogforge.infrastructure.export.PdfExportService;
import com.backlogforge.infrastructure.pdf.PdfExtractorService;
import com.backlogforge.web.dto.GenerateBacklogRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BacklogController.class)
class BacklogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GenerateBacklogUseCase generateBacklogUseCase;

    @MockBean
    private PdfExtractorService pdfExtractorService;

    @MockBean
    private MarkdownExportService markdownExportService;

    @MockBean
    private PdfExportService pdfExportService;

    @MockBean
    private CsvExportService csvExportService;

    private ProductBacklog sampleBacklog;

    @BeforeEach
    void setUp() {
        Task task = new Task("TASK-001", "Configurar BD", "Criar schema PostgreSQL", Priority.HIGH);
        UserStory story = new UserStory("US-001", "Cadastro", "Como usuário...", Priority.HIGH, 3, List.of("Critério 1"), List.of(task));
        Epic epic = new Epic("EPIC-001", "Módulo Base", "Descrição do épico", List.of(story));
        Sprint sprint = new Sprint("SPRINT-01", "Sprint 1", "Objetivo 1", List.of("US-001"));
        sampleBacklog = new ProductBacklog("Portal FATEC", "Resumo", List.of("Java 21", "Spring Boot"), List.of(epic), List.of(sprint));
    }

    @Test
    @DisplayName("POST /api/v1/backlog/generate deve retornar 200 e ProductBacklog válido")
    void shouldGenerateBacklogSuccessfully() throws Exception {
        GenerateBacklogRequest request = new GenerateBacklogRequest(
                "Portal FATEC",
                3,
                2,
                4,
                List.of("Java"),
                false,
                "Texto adicional"
        );

        when(generateBacklogUseCase.execute(any(GenerateBacklogRequest.class), eq(null)))
                .thenReturn(sampleBacklog);

        mockMvc.perform(post("/api/v1/backlog/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Portal FATEC"))
                .andExpect(jsonPath("$.epics[0].id").value("EPIC-001"))
                .andExpect(jsonPath("$.sprints[0].id").value("SPRINT-01"));
    }

    @Test
    @DisplayName("POST /api/v1/backlog/generate-with-pdf deve processar multipart e retornar 200")
    void shouldGenerateBacklogWithPdfSuccessfully() throws Exception {
        GenerateBacklogRequest request = new GenerateBacklogRequest(
                "Portal FATEC",
                2,
                2,
                3,
                List.of("Java"),
                false,
                "Contexto complementar"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile pdfPart = new MockMultipartFile(
                "files",
                "requisitos.pdf",
                "application/pdf",
                "%PDF-1.4 sample content".getBytes(StandardCharsets.UTF_8)
        );

        when(pdfExtractorService.extractTextFromPdfs(any())).thenReturn("Texto extraído do PDF");
        when(generateBacklogUseCase.execute(any(GenerateBacklogRequest.class), eq("Texto extraído do PDF")))
                .thenReturn(sampleBacklog);

        mockMvc.perform(multipart("/api/v1/backlog/generate-with-pdf")
                        .file(requestPart)
                        .file(pdfPart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Portal FATEC"));
    }

    @Test
    @DisplayName("POST /api/v1/backlog/export-markdown deve retornar 200 com cabeçalho de anexo UTF-8")
    void shouldExportMarkdownSuccessfully() throws Exception {
        when(markdownExportService.generateMarkdown(any(ProductBacklog.class)))
                .thenReturn("# Product Backlog: Portal FATEC\n\n## Epics...");

        mockMvc.perform(post("/api/v1/backlog/export-markdown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBacklog)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType("text/markdown;charset=UTF-8"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("# Product Backlog: Portal FATEC")));
    }

    @Test
    @DisplayName("POST /api/v1/backlog/export-pdf deve retornar 200 com MediaType application/pdf")
    void shouldExportPdfSuccessfully() throws Exception {
        byte[] fakePdf = "%PDF-1.4 binary content".getBytes(StandardCharsets.UTF_8);
        when(pdfExportService.generatePdf(any(ProductBacklog.class))).thenReturn(fakePdf);

        mockMvc.perform(post("/api/v1/backlog/export-pdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBacklog)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(fakePdf));
    }

    @Test
    @DisplayName("POST /api/v1/backlog/export-csv deve retornar 200 com MediaType text/csv e UTF-8")
    void shouldExportCsvSuccessfully() throws Exception {
        byte[] fakeCsv = "\uFEFFIssue Type,Issue Id,Parent Id,Summary,Description,Priority,Story Points,Sprint,Epic Name\n".getBytes(StandardCharsets.UTF_8);
        when(csvExportService.generateCsv(any(ProductBacklog.class))).thenReturn(fakeCsv);

        mockMvc.perform(post("/api/v1/backlog/export-csv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBacklog)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentType("text/csv;charset=UTF-8"))
                .andExpect(content().bytes(fakeCsv));
    }
}
