package com.backlogforge.infrastructure.export;

import com.backlogforge.domain.Epic;
import com.backlogforge.domain.ProductBacklog;
import com.backlogforge.domain.Sprint;
import com.backlogforge.domain.Task;
import com.backlogforge.domain.UserStory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela geração de arquivos PDF profissionais do Product Backlog.
 */
@Service
public class PdfExportService {

    private static final Logger log = LoggerFactory.getLogger(PdfExportService.class);

    private static final float MARGIN_LEFT = 40f;
    private static final float MARGIN_RIGHT = 40f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT;

    private static final float TOP_MARGIN = 40f;
    private static final float BOTTOM_MARGIN = 45f;

    // Cores da Identidade Visual (BacklogForge Slate/Indigo/Emerald)
    private static final Color COLOR_PRIMARY = new Color(30, 41, 59);     // Slate 800
    private static final Color COLOR_ACCENT = new Color(79, 70, 229);     // Indigo 600
    private static final Color COLOR_TEXT_DARK = new Color(15, 23, 42);   // Slate 900
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);// Slate 500
    private static final Color COLOR_BG_LIGHT = new Color(248, 250, 252);  // Slate 50
    private static final Color COLOR_BORDER = new Color(226, 232, 240);    // Slate 200

    private static final Color COLOR_CRITICAL = new Color(225, 29, 72);   // Rose 600
    private static final Color COLOR_HIGH = new Color(234, 88, 12);       // Orange 600
    private static final Color COLOR_MEDIUM = new Color(202, 138, 4);     // Yellow 600
    private static final Color COLOR_LOW = new Color(16, 185, 129);       // Emerald 500

    private PDFont fontRegular;
    private PDFont fontBold;
    private PDFont fontOblique;

    public PdfExportService() {
        try {
            this.fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            this.fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            this.fontOblique = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
        } catch (Exception e) {
            log.error("Erro ao carregar fontes padrão do PDFBox: {}", e.getMessage(), e);
        }
    }

    public byte[] generatePdf(ProductBacklog backlog) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PageContext ctx = new PageContext(document);
            ctx.newPage();

            // 1. Cabeçalho do Documento
            drawHeaderBanner(ctx, backlog.projectName());
            drawProjectSummary(ctx, backlog);

            // Map de US por ID para exibição no planejamento de Sprints
            Map<String, UserStory> userStoryMap = backlog.epics().stream()
                    .flatMap(e -> e.userStories().stream())
                    .collect(Collectors.toMap(UserStory::id, us -> us, (us1, us2) -> us1));

            // 2. Seção de Épicos e User Stories
            drawSectionTitle(ctx, "ÉPICOS E USER STORIES");

            for (Epic epic : backlog.epics()) {
                drawEpicHeader(ctx, epic);

                for (UserStory us : epic.userStories()) {
                    drawUserStoryCard(ctx, us);
                }
            }

            // 3. Seção de Planejamento de Sprints
            if (backlog.sprints() != null && !backlog.sprints().isEmpty()) {
                drawSectionTitle(ctx, "PLANEJAMENTO DE SPRINTS");

                for (Sprint sprint : backlog.sprints()) {
                    drawSprintCard(ctx, sprint, userStoryMap);
                }
            }

            // Adicionar numeração de página ao fechar
            ctx.closeCurrentStream();
            addPageNumbers(document);

            document.save(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Erro ao gerar PDF do Product Backlog: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao gerar arquivo PDF do Backlog: " + e.getMessage(), e);
        }
    }

    private void drawHeaderBanner(PageContext ctx, String projectName) throws IOException {
        float bannerHeight = 60f;
        ctx.ensureSpace(bannerHeight + 15f);

        float y = ctx.currentY - bannerHeight;

        // Fundo Banner
        ctx.stream.setNonStrokingColor(COLOR_PRIMARY);
        ctx.stream.addRect(MARGIN_LEFT, y, CONTENT_WIDTH, bannerHeight);
        ctx.stream.fill();

        // Subtítulo Superior
        ctx.stream.setNonStrokingColor(new Color(165, 180, 252));
        ctx.stream.setFont(fontBold, 8);
        ctx.stream.beginText();
        ctx.stream.newLineAtOffset(MARGIN_LEFT + 15, y + bannerHeight - 18);
        ctx.stream.showText("BACKLOGFORGE — PRODUCT BACKLOG");
        ctx.stream.endText();

        // Nome do Projeto
        ctx.stream.setNonStrokingColor(Color.WHITE);
        ctx.stream.setFont(fontBold, 18);
        ctx.stream.beginText();
        ctx.stream.newLineAtOffset(MARGIN_LEFT + 15, y + 16);
        ctx.stream.showText(sanitize(truncateText(projectName, fontBold, 18, CONTENT_WIDTH - 30)));
        ctx.stream.endText();

        ctx.currentY = y - 15f;
    }

    private void drawProjectSummary(PageContext ctx, ProductBacklog backlog) throws IOException {
        if (backlog.summary() != null && !backlog.summary().isBlank()) {
            List<String> lines = wrapText(backlog.summary(), fontOblique, 9.5f, CONTENT_WIDTH - 20);
            float boxHeight = (lines.size() * 13f) + 16f;
            ctx.ensureSpace(boxHeight + 10f);

            float y = ctx.currentY - boxHeight;

            // Box de fundo
            ctx.stream.setNonStrokingColor(COLOR_BG_LIGHT);
            ctx.stream.addRect(MARGIN_LEFT, y, CONTENT_WIDTH, boxHeight);
            ctx.stream.fill();

            // Borda esquerda colorida
            ctx.stream.setNonStrokingColor(COLOR_ACCENT);
            ctx.stream.addRect(MARGIN_LEFT, y, 4f, boxHeight);
            ctx.stream.fill();

            // Texto do resumo
            ctx.stream.setNonStrokingColor(COLOR_TEXT_DARK);
            ctx.stream.setFont(fontOblique, 9.5f);

            float textY = y + boxHeight - 16f;
            for (String line : lines) {
                ctx.stream.beginText();
                ctx.stream.newLineAtOffset(MARGIN_LEFT + 12f, textY);
                ctx.stream.showText(sanitize(line));
                ctx.stream.endText();
                textY -= 13f;
            }

            ctx.currentY = y - 15f;
        }

        // Badging de Stack de Tecnologias
        if (backlog.suggestedTechnologies() != null && !backlog.suggestedTechnologies().isEmpty()) {
            String techText = "Tecnologias: " + String.join(" • ", backlog.suggestedTechnologies());
            List<String> techLines = wrapText(techText, fontBold, 8.5f, CONTENT_WIDTH);

            ctx.ensureSpace((techLines.size() * 12f) + 10f);
            ctx.stream.setNonStrokingColor(COLOR_TEXT_MUTED);
            ctx.stream.setFont(fontBold, 8.5f);

            for (String line : techLines) {
                ctx.stream.beginText();
                ctx.stream.newLineAtOffset(MARGIN_LEFT, ctx.currentY - 10f);
                ctx.stream.showText(sanitize(line));
                ctx.stream.endText();
                ctx.currentY -= 12f;
            }
            ctx.currentY -= 10f;
        }
    }

    private void drawSectionTitle(PageContext ctx, String title) throws IOException {
        ctx.ensureSpace(35f);

        float barHeight = 22f;
        float y = ctx.currentY - barHeight;

        ctx.stream.setNonStrokingColor(COLOR_ACCENT);
        ctx.stream.addRect(MARGIN_LEFT, y, CONTENT_WIDTH, barHeight);
        ctx.stream.fill();

        ctx.stream.setNonStrokingColor(Color.WHITE);
        ctx.stream.setFont(fontBold, 10);
        ctx.stream.beginText();
        ctx.stream.newLineAtOffset(MARGIN_LEFT + 10f, y + 6f);
        ctx.stream.showText(sanitize(title));
        ctx.stream.endText();

        ctx.currentY = y - 15f;
    }

    private void drawEpicHeader(PageContext ctx, Epic epic) throws IOException {
        List<String> titleLines = wrapText(epic.id() + " — " + epic.title(), fontBold, 12f, CONTENT_WIDTH);
        List<String> descLines = epic.description() != null ? wrapText(epic.description(), fontRegular, 9f, CONTENT_WIDTH) : List.of();

        float requiredHeight = (titleLines.size() * 15f) + (descLines.size() * 12f) + 10f;
        ctx.ensureSpace(requiredHeight + 10f);

        // Título do Épico
        ctx.stream.setNonStrokingColor(COLOR_PRIMARY);
        ctx.stream.setFont(fontBold, 12f);
        for (String line : titleLines) {
            ctx.stream.beginText();
            ctx.stream.newLineAtOffset(MARGIN_LEFT, ctx.currentY - 12f);
            ctx.stream.showText(sanitize(line));
            ctx.stream.endText();
            ctx.currentY -= 15f;
        }

        // Descrição do Épico
        if (!descLines.isEmpty()) {
            ctx.stream.setNonStrokingColor(COLOR_TEXT_MUTED);
            ctx.stream.setFont(fontRegular, 9f);
            for (String line : descLines) {
                ctx.stream.beginText();
                ctx.stream.newLineAtOffset(MARGIN_LEFT, ctx.currentY - 9f);
                ctx.stream.showText(sanitize(line));
                ctx.stream.endText();
                ctx.currentY -= 12f;
            }
        }

        // Linha divisória
        ctx.stream.setStrokingColor(COLOR_BORDER);
        ctx.stream.setLineWidth(0.8f);
        ctx.stream.moveTo(MARGIN_LEFT, ctx.currentY - 4f);
        ctx.stream.lineTo(MARGIN_LEFT + CONTENT_WIDTH, ctx.currentY - 4f);
        ctx.stream.stroke();

        ctx.currentY -= 12f;
    }

    private void drawUserStoryCard(PageContext ctx, UserStory us) throws IOException {
        List<String> titleLines = wrapText(us.id() + " — " + us.title(), fontBold, 10.5f, CONTENT_WIDTH - 120);
        List<String> descLines = us.description() != null ? wrapText(us.description(), fontRegular, 9f, CONTENT_WIDTH - 20) : List.of();

        float minCardHeight = 40f + (titleLines.size() * 13f) + (descLines.size() * 12f);

        // Calcular altura de critérios de aceitação e tasks
        int criteriaCount = us.acceptanceCriteria() != null ? us.acceptanceCriteria().size() : 0;
        int tasksCount = us.tasks() != null ? us.tasks().size() : 0;

        minCardHeight += (criteriaCount * 13f) + (tasksCount * 14f) + 15f;

        ctx.ensureSpace(Math.min(minCardHeight, 180f));

        float startY = ctx.currentY;

        // Cabeçalho da US (ID + Título + Badges)
        ctx.stream.setNonStrokingColor(COLOR_TEXT_DARK);
        ctx.stream.setFont(fontBold, 10.5f);

        float usTitleY = ctx.currentY - 12f;
        for (String line : titleLines) {
            ctx.stream.beginText();
            ctx.stream.newLineAtOffset(MARGIN_LEFT + 5f, usTitleY);
            ctx.stream.showText(sanitize(line));
            ctx.stream.endText();
            usTitleY -= 13f;
        }

        // Priority & Points Badge no canto direito
        String priorityStr = us.priority() != null ? us.priority().toString() : "MEDIUM";
        String pointsStr = (us.storyPoints() != null ? us.storyPoints() : "-") + " pts";
        String badgeText = "[" + priorityStr + " | " + pointsStr + "]";

        ctx.stream.setNonStrokingColor(getPriorityColor(priorityStr));
        ctx.stream.setFont(fontBold, 9f);
        ctx.stream.beginText();
        ctx.stream.newLineAtOffset(MARGIN_LEFT + CONTENT_WIDTH - 115f, ctx.currentY - 12f);
        ctx.stream.showText(sanitize(badgeText));
        ctx.stream.endText();

        ctx.currentY = usTitleY - 4f;

        // Descrição da US
        if (!descLines.isEmpty()) {
            ctx.stream.setNonStrokingColor(COLOR_TEXT_MUTED);
            ctx.stream.setFont(fontRegular, 9f);
            for (String line : descLines) {
                ctx.ensureSpace(14f);
                ctx.stream.beginText();
                ctx.stream.newLineAtOffset(MARGIN_LEFT + 5f, ctx.currentY - 9f);
                ctx.stream.showText(sanitize(line));
                ctx.stream.endText();
                ctx.currentY -= 12f;
            }
            ctx.currentY -= 4f;
        }

        // Critérios de Aceitação
        if (us.acceptanceCriteria() != null && !us.acceptanceCriteria().isEmpty()) {
            ctx.ensureSpace(20f);
            ctx.stream.setNonStrokingColor(COLOR_PRIMARY);
            ctx.stream.setFont(fontBold, 8.5f);
            ctx.stream.beginText();
            ctx.stream.newLineAtOffset(MARGIN_LEFT + 5f, ctx.currentY - 9f);
            ctx.stream.showText("Criterios de Aceitacao:");
            ctx.stream.endText();
            ctx.currentY -= 13f;

            ctx.stream.setFont(fontRegular, 8.5f);
            ctx.stream.setNonStrokingColor(COLOR_TEXT_DARK);
            for (String crit : us.acceptanceCriteria()) {
                List<String> critLines = wrapText("• " + crit, fontRegular, 8.5f, CONTENT_WIDTH - 20);
                for (String line : critLines) {
                    ctx.ensureSpace(13f);
                    ctx.stream.beginText();
                    ctx.stream.newLineAtOffset(MARGIN_LEFT + 12f, ctx.currentY - 8f);
                    ctx.stream.showText(sanitize(line));
                    ctx.stream.endText();
                    ctx.currentY -= 12f;
                }
            }
            ctx.currentY -= 4f;
        }

        // Tasks
        if (us.tasks() != null && !us.tasks().isEmpty()) {
            ctx.ensureSpace(20f);
            ctx.stream.setNonStrokingColor(COLOR_PRIMARY);
            ctx.stream.setFont(fontBold, 8.5f);
            ctx.stream.beginText();
            ctx.stream.newLineAtOffset(MARGIN_LEFT + 5f, ctx.currentY - 9f);
            ctx.stream.showText("Tasks:");
            ctx.stream.endText();
            ctx.currentY -= 13f;

            for (Task task : us.tasks()) {
                String taskLabel = "[ ] " + task.id() + ": " + task.title() + (task.description() != null ? " - " + task.description() : "");
                List<String> taskLines = wrapText(taskLabel, fontRegular, 8.5f, CONTENT_WIDTH - 25);
                ctx.stream.setFont(fontRegular, 8.5f);
                ctx.stream.setNonStrokingColor(COLOR_TEXT_DARK);
                for (String line : taskLines) {
                    ctx.ensureSpace(13f);
                    ctx.stream.beginText();
                    ctx.stream.newLineAtOffset(MARGIN_LEFT + 12f, ctx.currentY - 8f);
                    ctx.stream.showText(sanitize(line));
                    ctx.stream.endText();
                    ctx.currentY -= 12f;
                }
            }
            ctx.currentY -= 4f;
        }

        // Moldura do Card de User Story
        float endY = ctx.currentY;
        float cardHeight = startY - endY;

        ctx.stream.setStrokingColor(COLOR_BORDER);
        ctx.stream.setLineWidth(0.5f);
        ctx.stream.addRect(MARGIN_LEFT, endY, CONTENT_WIDTH, cardHeight);
        ctx.stream.stroke();

        ctx.currentY -= 12f;
    }

    private void drawSprintCard(PageContext ctx, Sprint sprint, Map<String, UserStory> userStoryMap) throws IOException {
        int usCount = sprint.userStoryIds() != null ? sprint.userStoryIds().size() : 0;
        float estHeight = 40f + (usCount * 14f);
        ctx.ensureSpace(Math.min(estHeight, 150f));

        float startY = ctx.currentY;

        // Nome da Sprint
        ctx.stream.setNonStrokingColor(COLOR_PRIMARY);
        ctx.stream.setFont(fontBold, 11f);
        ctx.stream.beginText();
        ctx.stream.newLineAtOffset(MARGIN_LEFT + 8f, ctx.currentY - 14f);
        ctx.stream.showText(sanitize(sprint.name() + " (" + sprint.id() + ")"));
        ctx.stream.endText();

        ctx.currentY -= 18f;

        // Objetivo da Sprint
        if (sprint.goal() != null && !sprint.goal().isBlank()) {
            List<String> goalLines = wrapText("Objetivo: " + sprint.goal(), fontOblique, 9f, CONTENT_WIDTH - 20);
            ctx.stream.setNonStrokingColor(COLOR_TEXT_MUTED);
            ctx.stream.setFont(fontOblique, 9f);
            for (String line : goalLines) {
                ctx.ensureSpace(13f);
                ctx.stream.beginText();
                ctx.stream.newLineAtOffset(MARGIN_LEFT + 8f, ctx.currentY - 9f);
                ctx.stream.showText(sanitize(line));
                ctx.stream.endText();
                ctx.currentY -= 12f;
            }
            ctx.currentY -= 4f;
        }

        // Listagem de USs da Sprint
        if (sprint.userStoryIds() != null && !sprint.userStoryIds().isEmpty()) {
            for (String usId : sprint.userStoryIds()) {
                UserStory us = userStoryMap.get(usId);
                String label = us != null
                        ? "• " + us.id() + ": " + us.title() + " (" + us.storyPoints() + " pts, " + us.priority() + ")"
                        : "• " + usId;

                List<String> usLines = wrapText(label, fontRegular, 8.5f, CONTENT_WIDTH - 25);
                ctx.stream.setFont(fontRegular, 8.5f);
                ctx.stream.setNonStrokingColor(COLOR_TEXT_DARK);
                for (String line : usLines) {
                    ctx.ensureSpace(13f);
                    ctx.stream.beginText();
                    ctx.stream.newLineAtOffset(MARGIN_LEFT + 14f, ctx.currentY - 8f);
                    ctx.stream.showText(sanitize(line));
                    ctx.stream.endText();
                    ctx.currentY -= 12f;
                }
            }
        }

        ctx.currentY -= 6f;
        float cardHeight = startY - ctx.currentY;

        // Moldura Sprint Card
        ctx.stream.setStrokingColor(COLOR_ACCENT);
        ctx.stream.setLineWidth(0.8f);
        ctx.stream.addRect(MARGIN_LEFT, ctx.currentY, CONTENT_WIDTH, cardHeight);
        ctx.stream.stroke();

        ctx.currentY -= 12f;
    }

    private void addPageNumbers(PDDocument document) throws IOException {
        int pageCount = document.getNumberOfPages();
        for (int i = 0; i < pageCount; i++) {
            PDPage page = document.getPage(i);
            try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                cs.setNonStrokingColor(COLOR_TEXT_MUTED);
                cs.setFont(fontRegular, 8f);
                cs.beginText();
                cs.newLineAtOffset(PAGE_WIDTH - MARGIN_RIGHT - 80f, 25f);
                cs.showText(sanitize(String.format("Pagina %d de %d", i + 1, pageCount)));
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(MARGIN_LEFT, 25f);
                cs.showText(sanitize("BacklogForge — Gerado automaticamente com IA"));
                cs.endText();
            }
        }
    }

    private Color getPriorityColor(String priority) {
        if (priority == null) return COLOR_MEDIUM;
        switch (priority.toUpperCase()) {
            case "CRITICAL": return COLOR_CRITICAL;
            case "HIGH": return COLOR_HIGH;
            case "MEDIUM": return COLOR_MEDIUM;
            case "LOW": return COLOR_LOW;
            default: return COLOR_TEXT_MUTED;
        }
    }

    private List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        if (text == null || text.isBlank()) return List.of();
        List<String> lines = new ArrayList<>();
        String[] paragraphs = text.split("\r?\n");

        for (String paragraph : paragraphs) {
            String[] words = paragraph.split("\\s+");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String candidate = currentLine.isEmpty() ? word : currentLine + " " + word;
                float width = font.getStringWidth(sanitize(candidate)) / 1000f * fontSize;

                if (width > maxWidth && !currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    currentLine = new StringBuilder(candidate);
                }
            }
            if (!currentLine.isEmpty()) {
                lines.add(currentLine.toString());
            }
        }
        return lines;
    }

    private String truncateText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        if (text == null) return "";
        String clean = sanitize(text);
        if (font.getStringWidth(clean) / 1000f * fontSize <= maxWidth) {
            return clean;
        }
        while (!clean.isEmpty() && (font.getStringWidth(clean + "...") / 1000f * fontSize > maxWidth)) {
            clean = clean.substring(0, clean.length() - 1);
        }
        return clean + "...";
    }

    private String sanitize(String text) {
        if (text == null) return "";
        return text.replace("—", "-")
                .replace("–", "-")
                .replace("“", "\"")
                .replace("”", "\"")
                .replace("‘", "'")
                .replace("’", "'")
                .replace("•", "*")
                .replaceAll("[^\\x00-\\xFF]", "");
    }

    private static class PageContext {
        private final PDDocument document;
        private PDPage currentPage;
        private PDPageContentStream stream;
        private float currentY;

        public PageContext(PDDocument document) {
            this.document = document;
        }

        public void newPage() throws IOException {
            if (stream != null) {
                stream.close();
            }
            this.currentPage = new PDPage(PDRectangle.A4);
            this.document.addPage(this.currentPage);
            this.stream = new PDPageContentStream(document, currentPage);
            this.currentY = PAGE_HEIGHT - TOP_MARGIN;
        }

        public void ensureSpace(float requiredHeight) throws IOException {
            if (currentY - requiredHeight < BOTTOM_MARGIN) {
                newPage();
            }
        }

        public void closeCurrentStream() throws IOException {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
