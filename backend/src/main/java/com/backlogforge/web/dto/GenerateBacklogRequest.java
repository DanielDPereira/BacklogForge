package com.backlogforge.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Contrato de entrada para a solicitação de geração do Product Backlog.
 */
@Schema(description = "Objeto contendo os parâmetros determinísticos de geração do Product Backlog.")
public record GenerateBacklogRequest(

        @Schema(description = "Nome do projeto de software", example = "Portal do Aluno FATEC", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
        @NotBlank(message = "O nome do projeto é obrigatório.")
        @Size(max = 100, message = "O nome do projeto deve ter no máximo 100 caracteres.")
        String projectName,

        @Schema(description = "Quantidade exata de Sprints para planejamento (1 a 20)", example = "3", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", maximum = "20")
        @NotNull(message = "O número de Sprints é obrigatório.")
        @Min(value = 1, message = "O projeto deve ter no mínimo 1 Sprint.")
        @Max(value = 20, message = "O projeto pode ter no máximo 20 Sprints.")
        Integer sprintCount,

        @Schema(description = "Duração de cada Sprint em semanas (1 a 8)", example = "2", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", maximum = "8")
        @NotNull(message = "A duração da Sprint é obrigatória.")
        @Min(value = 1, message = "A duração da Sprint deve ser de no mínimo 1 semana.")
        @Max(value = 8, message = "A duração da Sprint deve ser de no máximo 8 semanas.")
        Integer sprintDurationWeeks,

        @Schema(description = "Tamanho da equipe de desenvolvimento (1 a 50 integrantes)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", maximum = "50")
        @NotNull(message = "O tamanho da equipe é obrigatório.")
        @Min(value = 1, message = "A equipe deve ter no mínimo 1 integrante.")
        @Max(value = 50, message = "A equipe pode ter no máximo 50 integrantes.")
        Integer teamSize,

        @Schema(description = "Lista de tecnologias predefinidas pelo usuário", example = "[\"Java 21\", \"Spring Boot\", \"React\", \"PostgreSQL\"]")
        List<String> technologies,

        @Schema(description = "Indica se a IA deve sugerir tecnologias complementares", example = "false", defaultValue = "false")
        Boolean suggestTechnologies,

        @Schema(description = "Texto livre complementar com requisitos ou regras de negócio adicionais", example = "O sistema deve possuir módulo de autenticação JWT e relatório em PDF.", maxLength = 10000)
        @Size(max = 10000, message = "O texto adicional pode ter no máximo 10.000 caracteres.")
        String additionalText
) {
    public GenerateBacklogRequest {
        if (suggestTechnologies == null) {
            suggestTechnologies = false;
        }
        if (technologies == null) {
            technologies = List.of();
        }
    }
}
