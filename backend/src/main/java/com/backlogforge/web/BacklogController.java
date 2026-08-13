package com.backlogforge.web;

import com.backlogforge.application.usecase.GenerateBacklogUseCase;
import com.backlogforge.domain.ProductBacklog;
import com.backlogforge.infrastructure.export.MarkdownExportService;
import com.backlogforge.infrastructure.export.PdfExportService;
import com.backlogforge.infrastructure.pdf.PdfExtractorService;
import com.backlogforge.web.dto.GenerateBacklogRequest;
import jakarta.validation.Valid;
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
 * Controller REST responsável por endpoints de geração, upload de documentos e exportação em Markdown e PDF.
 */
@RestController
@RequestMapping("/api/v1/backlog")
@CrossOrigin(origins = "*")
public class BacklogController {

    private final GenerateBacklogUseCase generateBacklogUseCase;
    private final PdfExtractorService pdfExtractorService;
    private final MarkdownExportService markdownExportService;
    private final PdfExportService pdfExportService;

    public BacklogController(
            GenerateBacklogUseCase generateBacklogUseCase,
            PdfExtractorService pdfExtractorService,
            MarkdownExportService markdownExportService,
            PdfExportService pdfExportService
    ) {
        this.generateBacklogUseCase = generateBacklogUseCase;
        this.pdfExtractorService = pdfExtractorService;
        this.markdownExportService = markdownExportService;
        this.pdfExportService = pdfExportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ProductBacklog> generateBacklog(
            @Valid @RequestBody GenerateBacklogRequest request
    ) {
        ProductBacklog backlog = generateBacklogUseCase.execute(request, null);
        return ResponseEntity.ok(backlog);
    }

    @PostMapping(value = "/generate-with-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductBacklog> generateBacklogWithPdf(
            @Valid @RequestPart("request") GenerateBacklogRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        String pdfText = pdfExtractorService.extractTextFromPdfs(files);
        ProductBacklog backlog = generateBacklogUseCase.execute(request, pdfText);
        return ResponseEntity.ok(backlog);
    }

    @PostMapping("/export-markdown")
    public ResponseEntity<byte[]> exportMarkdown(@RequestBody ProductBacklog backlog) {
        String markdown = markdownExportService.generateMarkdown(backlog);
        byte[] bytes = markdown.getBytes(StandardCharsets.UTF_8);

        String filename = (backlog.projectName() != null ? backlog.projectName().replaceAll("\\s+", "_") : "Backlog") + ".md";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/markdown; charset=UTF-8"))
                .body(bytes);
    }

    @PostMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestBody ProductBacklog backlog) {
        byte[] pdfBytes = pdfExportService.generatePdf(backlog);

        String filename = (backlog.projectName() != null ? backlog.projectName().replaceAll("\\s+", "_") : "Backlog") + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
