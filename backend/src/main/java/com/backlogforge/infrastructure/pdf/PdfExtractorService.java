package com.backlogforge.infrastructure.pdf;

import com.backlogforge.application.ai.AiService;
import com.backlogforge.domain.exception.InvalidDocumentException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * Serviço responsável por validar e extrair o conteúdo textual de arquivos PDF.
 * Suporta PDFs com texto vetorial e PDFs escaneados (com imagens) através de renderização visual e OCR via IA.
 */
@Service
public class PdfExtractorService {

    private static final Logger log = LoggerFactory.getLogger(PdfExtractorService.class);
    private final AiService aiService;

    public PdfExtractorService(AiService aiService) {
        this.aiService = aiService;
    }

    public String extractTextFromPdfs(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return "";
        }

        StringBuilder fullText = new StringBuilder();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            validatePdfFile(file);

            try {
                String extracted = processPdfDocument(file);
                fullText.append("--- INÍCIO DO DOCUMENTO ").append(i + 1).append(": ").append(file.getOriginalFilename()).append(" ---\n");
                fullText.append(extracted).append("\n");
                fullText.append("--- FIM DO DOCUMENTO ").append(i + 1).append(" ---\n\n");
            } catch (Exception e) {
                log.error("Erro ao extrair texto do PDF {}: {}", file.getOriginalFilename(), e.getMessage(), e);
                throw new InvalidDocumentException("Falha ao ler o arquivo PDF '" + file.getOriginalFilename() + "': " + e.getMessage(), e);
            }
        }

        return fullText.toString();
    }

    private String processPdfDocument(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Se o texto vetorial extraído for suficiente (>= 50 caracteres), utiliza o texto diretamente
            if (text != null && text.trim().length() >= 50) {
                log.info("Texto vetorial extraído com sucesso do PDF '{}' ({} caracteres)", file.getOriginalFilename(), text.trim().length());
                return text.trim();
            }

            // Se o texto vetorial for escasso ou vazio (PDF escaneado/imagem), faz OCR visual via IA
            log.warn("O PDF '{}' possui texto vetorial escasso ou é composto por imagens escaneadas. Iniciando transcrição por OCR visual via IA...", file.getOriginalFilename());
            return performOcrOnPdfPages(document, file.getOriginalFilename());
        }
    }

    private String performOcrOnPdfPages(PDDocument document, String filename) throws IOException {
        PDFRenderer renderer = new PDFRenderer(document);
        StringBuilder ocrResult = new StringBuilder();
        int totalPages = Math.min(document.getNumberOfPages(), 10);

        for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
            BufferedImage pageImage = renderer.renderImageWithDPI(pageIndex, 150);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(pageImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            log.info("Página {}/{} do PDF '{}' convertida em imagem PNG. Solicitando transcrição via IA...", pageIndex + 1, totalPages, filename);

            String pagePrompt = """
                    Analise a imagem a seguir correspondente à página %d do documento '%s'.
                    Transcreva com total precisão todo o conteúdo textual, tabelas, títulos, requisitos e tópicos visíveis.
                    Preserve a estrutura original das informações.
                    """.formatted(pageIndex + 1, filename);

            String transcribedText = aiService.generateWithImage(pagePrompt, base64Image, "image/png");
            ocrResult.append("[Página ").append(pageIndex + 1).append(" (Transcrição Visual)]\n").append(transcribedText).append("\n\n");
        }

        return ocrResult.toString();
    }

    private void validatePdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidDocumentException("O arquivo enviado está vazio.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new InvalidDocumentException("O arquivo '" + filename + "' não possui extensão .pdf válida.");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.equalsIgnoreCase("application/pdf") && !contentType.equalsIgnoreCase("application/x-pdf")) {
            log.warn("MIME type suspeito para o arquivo {}: {}", filename, contentType);
        }
    }
}
