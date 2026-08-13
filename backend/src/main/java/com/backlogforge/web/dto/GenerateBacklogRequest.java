package com.backlogforge.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Contrato de entrada para a solicitação de geração do Product Backlog.
 */
public record GenerateBacklogRequest(

        @NotBlank(message = "O nome do projeto é obrigatório.")
        @Size(max = 100, message = "O nome do projeto deve ter no máximo 100 caracteres.")
        String projectName,

        @NotNull(message = "O número de Sprints é obrigatório.")
        @Min(value = 1, message = "O projeto deve ter no mínimo 1 Sprint.")
        @Max(value = 20, message = "O projeto pode ter no máximo 20 Sprints.")
        Integer sprintCount,

        @NotNull(message = "A duração da Sprint é obrigatória.")
        @Min(value = 1, message = "A duração da Sprint deve ser de no mínimo 1 semana.")
        @Max(value = 8, message = "A duração da Sprint deve ser de no máximo 8 semanas.")
        Integer sprintDurationWeeks,

        @NotNull(message = "O tamanho da equipe é obrigatório.")
        @Min(value = 1, message = "A equipe deve ter no mínimo 1 integrante.")
        @Max(value = 50, message = "A equipe pode ter no máximo 50 integrantes.")
        Integer teamSize,

        List<String> technologies,

        Boolean suggestTechnologies,

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
