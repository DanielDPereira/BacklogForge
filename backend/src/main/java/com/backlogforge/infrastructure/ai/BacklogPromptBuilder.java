package com.backlogforge.infrastructure.ai;

import com.backlogforge.web.dto.GenerateBacklogRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Construtor de prompts estruturados para orientar o modelo de linguagem na geração do Product Backlog.
 */
@Component
public class BacklogPromptBuilder {

    public String buildPrompt(GenerateBacklogRequest request, String pdfExtractedText) {
        StringBuilder sb = new StringBuilder();

        sb.append("""
                Você é um especialista sênior em Engenharia de Software, Gestão de Produtos e metodologias Ágeis (Scrum/Kanban).
                Sua tarefa é analisar o contexto do projeto fornecido e gerar um Product Backlog altamente profissional, detalhado e completo.

                ### REGRAS OBRIGATÓRIAS DE GERAÇÃO:
                1. ESTRUTURA HIERÁRQUICA:
                   - O backlog deve conter Épicos (Epics).
                   - Cada Épico contém User Stories.
                   - Cada User Story contém uma lista de Tasks operacionais/técnicas e critérios de aceitação.
                   - Os IDs devem ser padronizados (ex: EPIC-001, US-001, TASK-001, SPRINT-01).

                2. CONTRATO DAS SPRINTS:
                   - Você DEVE gerar exatamente %d Sprints. Nem mais, nem menos.
                   - Cada Sprint possui um nome (ex: "Sprint 1"), um objetivo claro (goal) e uma lista de IDs das User Stories alocadas nessa Sprint (userStoryIds).
                   - As Sprints DEVEM referenciar as User Stories exclusivamente através de seus IDs (ex: ["US-001", "US-002"]), sem duplicar o conteúdo da User Story.
                   - Todas as User Stories criadas nos Épicos devem ser distribuídas de forma coerente entre as Sprints solicitadas.

                3. REGRAS DE TAREFAS E EQUIPE:
                   - O tamanho da equipe é de %d integrante(s). Calibre a granularidade e o nível de detalhe das Tasks para este tamanho de equipe.
                   - É ESTRITAMENTE PROIBIDO atribuir tarefas a pessoas específicas ou mencionar nomes de integrantes.

                4. TECNOLOGIAS:
                """.formatted(request.sprintCount(), request.teamSize()));

        if (Boolean.TRUE.equals(request.suggestTechnologies())) {
            if (request.technologies() != null && !request.technologies().isEmpty()) {
                sb.append("   - Tecnologias BASE informadas pelo usuário: ").append(formatTechnologies(request.technologies())).append("\n");
                sb.append("   - REGRA DE SUGESTÃO: O usuário solicitou sugestões complementares. Você DEVE MANTER OBRIGATORIAMENTE todas as tecnologias base informadas (ex: Java, Spring Boot, React, PostgreSQL) e COMPLEMENTAR a lista com bibliotecas, ferramentas de CI/CD, banco de dados ou frameworks auxiliares recomendados (ex: Docker, OpenAPI/Swagger, JUnit 5, TailwindCSS).\n");
                sb.append("   - PROIBIÇÃO DE CONTRADIÇÃO: É ESTRITAMENTE PROIBIDO substituir ou ignorar as tecnologias base do usuário por outras tecnologias incompatíveis (ex: NUNCA substitua Java/Spring por Python/Flask nas User Stories e Tasks quando Java/Spring foi informado).\n");
            } else {
                sb.append("   - O usuário solicitou que você sugira a melhor stack tecnológica para o projeto. Analise o escopo e preencha o campo `suggestedTechnologies` com tecnologias modernas, robustas e coerentes entre si.\n");
            }
        } else {
            sb.append("   - Tecnologias ESTRITAS especificadas pelo usuário: ").append(formatTechnologies(request.technologies())).append("\n");
            sb.append("   - Mantenha `suggestedTechnologies` contendo exatamente as tecnologias informadas pelo usuário. Todas as User Stories e Tasks DEVEM utilizar EXCLUSIVAMENTE essa stack.\n");
        }

        sb.append("""
                
                5. ESTIMATIVAS E PRIORIDADES:
                   - Todas as User Stories devem ter estimativa em Story Points (utilize a sequência Fibonacci: 1, 2, 3, 5, 8, 13).
                   - Prioridades válidas para User Stories e Tasks: LOW, MEDIUM, HIGH, CRITICAL.

                ### PARÂMETROS DETERMINÍSTICOS DO PROJETO:
                - Nome do Projeto: %s
                - Número de Sprints: %d
                - Duração de cada Sprint: %d semana(s)
                - Tamanho da Equipe: %d integrante(s)
                """.formatted(
                request.projectName(),
                request.sprintCount(),
                request.sprintDurationWeeks(),
                request.teamSize()
        ));

        if (request.additionalText() != null && !request.additionalText().isBlank()) {
            sb.append("\n### CONTEXTO COMPLEMENTAR EM TEXTO:\n");
            sb.append(request.additionalText().trim()).append("\n");
        }

        if (pdfExtractedText != null && !pdfExtractedText.isBlank()) {
            sb.append("\n### CONTEXTO EXTRAÍDO DOS DOCUMENTOS (PDF):\n");
            sb.append(pdfExtractedText.trim()).append("\n");
        }

        return sb.toString();
    }

    private String formatTechnologies(List<String> technologies) {
        if (technologies == null || technologies.isEmpty()) {
            return "Nenhuma tecnologia especificada previamente.";
        }
        return String.join(", ", technologies);
    }
}
