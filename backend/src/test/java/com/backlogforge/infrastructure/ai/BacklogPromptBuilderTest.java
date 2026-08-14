package com.backlogforge.infrastructure.ai;

import com.backlogforge.web.dto.GenerateBacklogRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BacklogPromptBuilderTest {

    private BacklogPromptBuilder promptBuilder;

    @BeforeEach
    void setUp() {
        promptBuilder = new BacklogPromptBuilder();
    }

    @Test
    @DisplayName("Deve incluir a stack base e proibir substituições quando suggestTechnologies for true com tecnologias informadas")
    void shouldPreserveBaseStackAndAllowSuggestions() {
        GenerateBacklogRequest request = new GenerateBacklogRequest(
                "API Integradora FATEC",
                3,
                3,
                7,
                List.of("Java", "Spring Boot", "React", "PostgreSQL"),
                true,
                "Contexto de teste"
        );

        String prompt = promptBuilder.buildPrompt(request, null);

        assertTrue(prompt.contains("Tecnologias BASE informadas pelo usuário: Java, Spring Boot, React, PostgreSQL"));
        assertTrue(prompt.contains("REGRA DE SUGESTÃO"));
        assertTrue(prompt.contains("PROIBIÇÃO DE CONTRADIÇÃO"));
    }

    @Test
    @DisplayName("Deve instruir uso estrito da stack informada quando suggestTechnologies for false")
    void shouldEnforceStrictStackWhenSuggestTechnologiesIsFalse() {
        GenerateBacklogRequest request = new GenerateBacklogRequest(
                "API Integradora FATEC",
                3,
                3,
                7,
                List.of("Java", "Spring Boot"),
                false,
                "Contexto de teste"
        );

        String prompt = promptBuilder.buildPrompt(request, null);

        assertTrue(prompt.contains("Tecnologias ESTRITAS especificadas pelo usuário: Java, Spring Boot"));
        assertFalse(prompt.contains("PROIBIÇÃO DE CONTRADIÇÃO"));
    }
}
