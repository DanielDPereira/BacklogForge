package com.backlogforge.infrastructure.ai;

import com.backlogforge.domain.exception.AiProviderException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeminiAiServiceTest {

    private ObjectMapper objectMapper;
    private ApiKeyManager apiKeyManager;
    private GeminiAiService geminiAiService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        apiKeyManager = new ApiKeyManager("test-key-1, test-key-2", null);
        geminiAiService = new GeminiAiService(objectMapper, apiKeyManager, "gemini-1.5-flash-latest");
    }

    @Test
    @DisplayName("Deve falhar graciosamente se nenhuma chave responder com sucesso")
    void shouldThrowAiProviderExceptionWhenKeysFail() {
        assertThrows(AiProviderException.class, () -> geminiAiService.generate("Prompt de teste"));
    }
}
