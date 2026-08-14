package com.backlogforge.web;

import com.backlogforge.application.usecase.GenerateBacklogUseCase;
import com.backlogforge.domain.ProductBacklog;
import com.backlogforge.infrastructure.export.CsvExportService;
import com.backlogforge.infrastructure.export.MarkdownExportService;
import com.backlogforge.infrastructure.export.PdfExportService;
import com.backlogforge.infrastructure.pdf.PdfExtractorService;
import com.backlogforge.web.dto.GenerateBacklogRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controller REST responsável por endpoints de geração, upload de documentos e exportação em Markdown, PDF e CSV (Jira/Trello).
 */
@RestController
@RequestMapping("/api/v1/backlog")
@CrossOrigin(origins = "*")
@Tag(name = "Product Backlog Controller", description = "Endpoints REST para geração assistida de Product Backlogs com IA, processamento de PDFs e exportação multiformato.")
public class BacklogController {

    private final GenerateBacklogUseCase generateBacklogUseCase;
    private final PdfExtractorService pdfExtractorService;
    private final MarkdownExportService markdownExportService;
    private final PdfExportService pdfExportService;
    private final CsvExportService csvExportService;

    public BacklogController(
            GenerateBacklogUseCase generateBacklogUseCase,
            PdfExtractorService pdfExtractorService,
            MarkdownExportService markdownExportService,
            PdfExportService pdfExportService,
            CsvExportService csvExportService
    ) {
        this.generateBacklogUseCase = generateBacklogUseCase;
        this.pdfExtractorService = pdfExtractorService;
        this.markdownExportService = markdownExportService;
        this.pdfExportService = pdfExportService;
        this.csvExportService = csvExportService;
    }

    @PostMapping("/generate")
    @Operation(
            summary = "Gerar Product Backlog (apenas texto/parâmetros)",
            description = "Gera um Product Backlog estruturado contendo Épicos, User Stories, Tasks, critérios de aceitação e Sprints a partir dos parâmetros informados e texto adicional."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product Backlog gerado com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductBacklog.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros de requisição inválidos."),
            @ApiResponse(responseCode = "422", description = "A IA gerou um backlog estruturalmente inconsistente."),
            @ApiResponse(responseCode = "502", description = "Erro de comunicação ou limite de quota no provedor de IA (Gemini API).")
    })
    public ResponseEntity<ProductBacklog> generateBacklog(
            @Valid @RequestBody GenerateBacklogRequest request
    ) {
        ProductBacklog backlog = generateBacklogUseCase.execute(request, null);
        return ResponseEntity.ok(backlog);
    }

    @PostMapping(value = "/generate-with-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Gerar Product Backlog com anexos em PDF",
            description = "Gera um Product Backlog combinando parâmetros determinísticos com o texto extraído de arquivos PDF (suportando documentos vetoriais e PDFs escaneados via OCR visual multimodal)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product Backlog gerado com sucesso a partir dos PDFs e parâmetros.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductBacklog.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou extensão de arquivo incorreta (apenas .pdf aceito)."),
            @ApiResponse(responseCode = "422", description = "Inconsistência estrutural no backlog gerado."),
            @ApiResponse(responseCode = "502", description = "Erro no provedor de IA (Gemini API).")
    })
    public ResponseEntity<ProductBacklog> generateBacklogWithPdf(
            @Valid @RequestPart("request") GenerateBacklogRequest request,
            @Parameter(description = "Arquivos PDF anexados (vetoriais ou escaneados)", required = false)
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        String pdfText = pdfExtractorService.extractTextFromPdfs(files);
        ProductBacklog backlog = generateBacklogUseCase.execute(request, pdfText);
        return ResponseEntity.ok(backlog);
    }

    @PostMapping("/export-markdown")
    @Operation(
            summary = "Exportar Backlog para formato Markdown (.md)",
            description = "Recebe um objeto Product Backlog estruturado e gera o conteúdo formatado em arquivo Markdown (.md) para download."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo Markdown (.md) gerado com sucesso.", content = @Content(mediaType = "text/markdown; charset=UTF-8")),
            @ApiResponse(responseCode = "500", description = "Erro ao processar o modelo para geração do Markdown.")
    })
    public ResponseEntity<byte[]> exportMarkdown(
            @RequestBody ProductBacklog backlog
    ) {
        String markdown = markdownExportService.generateMarkdown(backlog);
        byte[] bytes = markdown.getBytes(StandardCharsets.UTF_8);

        String rawName = (backlog.projectName() != null ? backlog.projectName().replaceAll("\\s+", "_") : "Backlog") + ".md";
        ContentDisposition contentDisposition = ContentDisposition
                .attachment()
                .filename(rawName, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.parseMediaType("text/markdown; charset=UTF-8"))
                .body(bytes);
    }

    @PostMapping("/export-pdf")
    @Operation(
            summary = "Exportar Backlog para formato PDF (.pdf)",
            description = "Recebe um objeto Product Backlog estruturado e gera um documento PDF (.pdf) diagramado profissionalmente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo PDF (.pdf) gerado com sucesso.", content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "500", description = "Erro ao gerar a diagramação PDF do Backlog.")
    })
    public ResponseEntity<byte[]> exportPdf(
            @RequestBody ProductBacklog backlog
    ) {
        byte[] pdfBytes = pdfExportService.generatePdf(backlog);

        String rawName = (backlog.projectName() != null ? backlog.projectName().replaceAll("\\s+", "_") : "Backlog") + ".pdf";
        ContentDisposition contentDisposition = ContentDisposition
                .attachment()
                .filename(rawName, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/export-csv")
    @Operation(
            summary = "Exportar Backlog para formato CSV universal (Jira / Trello)",
            description = "Recebe um objeto Product Backlog estruturado e gera um arquivo CSV (.csv) formatado e otimizado para importação direta em ferramentas como Jira, Trello e Azure DevOps."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo CSV (.csv) gerado com sucesso.", content = @Content(mediaType = "text/csv; charset=UTF-8")),
            @ApiResponse(responseCode = "500", description = "Erro ao gerar a estrutura CSV do Backlog.")
    })
    public ResponseEntity<byte[]> exportCsv(
            @RequestBody ProductBacklog backlog
    ) {
        byte[] csvBytes = csvExportService.generateCsv(backlog);

        String rawName = (backlog.projectName() != null ? backlog.projectName().replaceAll("\\s+", "_") : "Backlog") + "_Jira.csv";
        ContentDisposition contentDisposition = ContentDisposition
                .attachment()
                .filename(rawName, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }
}
