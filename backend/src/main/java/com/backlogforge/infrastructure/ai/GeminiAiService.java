package com.backlogforge.infrastructure.ai;

import com.backlogforge.application.ai.AiService;
import com.backlogforge.domain.exception.AiProviderException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Implementação do serviço de IA integrado com a API do Google Gemini.
 * Suporta rotação automática de múltiplas chaves e resiliência a erros de cota (HTTP 429).
 */
@Service
public class GeminiAiService implements AiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiService.class);
    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ApiKeyManager apiKeyManager;

    public GeminiAiService(ObjectMapper objectMapper, ApiKeyManager apiKeyManager) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
        this.apiKeyManager = apiKeyManager;
    }

    @Override
    public String generate(String prompt) {
        int maxAttempts = Math.max(1, apiKeyManager.getAvailableKeysCount());
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            String activeKey = apiKeyManager.getActiveKey();

            try {
                String url = GEMINI_ENDPOINT + "?key=" + activeKey;

                Map<String, Object> requestBody = Map.of(
                        "contents", List.of(
                                Map.of("parts", List.of(
                                        Map.of("text", prompt)
                                ))
                        )
                );

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return extractTextFromGeminiResponse(response.getBody());
                }
            } catch (HttpStatusCodeException e) {
                lastException = e;
                log.warn("Erro HTTP {} na chamada ao Gemini API (tentativa {}/{}): {}",
                        e.getStatusCode(), attempt, maxAttempts, e.getResponseBodyAsString());

                if (e.getStatusCode().value() == 429 || isQuotaError(e.getResponseBodyAsString())) {
                    apiKeyManager.markKeyExhausted(activeKey);
                    continue; // Tenta a próxima chave
                }
                throw new AiProviderException("Erro do provedor Gemini (HTTP " + e.getStatusCode() + "): " + e.getMessage(), e);
            } catch (Exception e) {
                lastException = e;
                log.error("Erro inesperado na chamada ao Gemini (tentativa {}/{}): {}", attempt, maxAttempts, e.getMessage());
                if (isQuotaError(e.getMessage())) {
                    apiKeyManager.markKeyExhausted(activeKey);
                    continue;
                }
                throw new AiProviderException("Erro ao se comunicar com o provedor Gemini: " + e.getMessage(), e);
            }
        }

        throw new AiProviderException("Todas as tentativas de geração com as chaves configuradas falharam.", lastException);
    }

    @Override
    public <T> T generateStructured(String prompt, Class<T> responseType) {
        String structuredPrompt = prompt + """
                
                
                ### INSTRUÇÃO CRÍTICA DE FORMATO DE SAÍDA:
                Responda EXCLUSIVAMENTE com o objeto JSON correspondente, sem explicações em linguagem natural, sem introduções e sem marcações Markdown como ```json.
                O formato da resposta deve ser estritamente um JSON válido.
                """;

        String jsonResponse = generate(structuredPrompt);
        String cleanedJson = cleanJsonResponse(jsonResponse);

        try {
            return objectMapper.readValue(cleanedJson, responseType);
        } catch (Exception e) {
            log.error("Erro ao desserializar JSON retornado pelo Gemini para {}: {}", responseType.getSimpleName(), e.getMessage());
            log.debug("JSON recebido do modelo: {}", jsonResponse);
            throw new AiProviderException("A IA gerou uma resposta estruturalmente inválida para o schema esperado: " + e.getMessage(), e);
        }
    }

    private boolean isQuotaError(String errorDetails) {
        if (errorDetails == null) return false;
        String lower = errorDetails.toLowerCase();
        return lower.contains("quota") || lower.contains("resource_exhausted") || lower.contains("rate limit") || lower.contains("429");
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromGeminiResponse(Map responseBody) {
        try {
            List candidates = (List) responseBody.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map firstCandidate = (Map) candidates.get(0);
                Map content = (Map) firstCandidate.get("content");
                List parts = (List) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    Map firstPart = (Map) parts.get(0);
                    return (String) firstPart.get("text");
                }
            }
        } catch (Exception e) {
            log.error("Erro ao extrair texto da estrutura de resposta do Gemini: {}", e.getMessage());
        }
        throw new AiProviderException("Formato de resposta inesperado retornado pela API do Gemini.");
    }

    private String cleanJsonResponse(String jsonResponse) {
        if (jsonResponse == null) {
            return "";
        }
        String cleaned = jsonResponse.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }
}
