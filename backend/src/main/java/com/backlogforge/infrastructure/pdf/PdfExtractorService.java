package com.backlogforge.infrastructure.pdf;

import com.backlogforge.domain.exception.InvalidDocumentException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Serviço responsável por validar e extrair o conteúdo textual de arquivos PDF.
 */
@Service
public class PdfExtractorService {

    private static final Logger log = LoggerFactory.getLogger(PdfExtractorService.class);

    public String extractTextFromPdfs(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return "";
        }

        StringBuilder fullText = new StringBuilder();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            validatePdfFile(file);

            try {
                String extracted = extractTextFromFile(file);
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

    private String extractTextFromFile(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
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
