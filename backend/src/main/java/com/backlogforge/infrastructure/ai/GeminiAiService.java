package com.backlogforge.infrastructure.ai;

import com.backlogforge.application.ai.AiService;
import com.backlogforge.domain.exception.AiProviderException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementação do serviço de IA integrado com a API do Google Gemini.
 * Suporta rotação de chaves, resiliência a limite de cota (HTTP 429) e fallback inteligente de modelos (HTTP 404).
 */
@Service
public class GeminiAiService implements AiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiService.class);
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    private static final List<String> FALLBACK_MODELS = List.of(
            "gemini-2.5-flash",
            "gemini-2.5-pro",
            "gemini-3.6-flash",
            "gemini-flash-latest",
            "gemini-3.5-flash",
            "gemini-1.5-flash-latest",
            "gemini-1.5-flash",
            "gemini-pro"
    );

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ApiKeyManager apiKeyManager;
    private final String configuredModel;

    public GeminiAiService(
            ObjectMapper objectMapper,
            ApiKeyManager apiKeyManager,
            @Value("${spring.ai.gemini.model:${GEMINI_MODEL:gemini-2.5-flash}}") String configuredModel
    ) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
        this.apiKeyManager = apiKeyManager;
        this.configuredModel = configuredModel;
    }

    @Override
    public String generate(String prompt) {
        int maxKeyAttempts = Math.max(1, apiKeyManager.getAvailableKeysCount());
        List<String> candidateModels = getCandidateModels();
        Exception lastException = null;

        for (int keyAttempt = 1; keyAttempt <= maxKeyAttempts; keyAttempt++) {
            String activeKey = apiKeyManager.getActiveKey();

            for (String modelName : candidateModels) {
                try {
                    String url = GEMINI_BASE_URL + modelName + ":generateContent?key=" + activeKey;
                    log.debug("Chamando Gemini API com modelo '{}' (tentativa de chave {}/{})", modelName, keyAttempt, maxKeyAttempts);

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

                    if (e.getStatusCode().value() == 404) {
                        log.info("Modelo Gemini '{}' não encontrado para esta API Key (HTTP 404). Tentando modelo alternativo...", modelName);
                        continue;
                    }

                    if (isTemporaryServerError(e.getStatusCode().value(), e.getResponseBodyAsString())) {
                        log.warn("Modelo Gemini '{}' temporariamente sobrecarregado ou indisponível (HTTP {}). Alternando automaticamente para o próximo modelo candidato...", modelName, e.getStatusCode().value());
                        continue;
                    }

                    if (e.getStatusCode().value() == 429 || isQuotaError(e.getResponseBodyAsString())) {
                        log.warn("Limite de cota atingido na chave atual (HTTP 429). Rotacionando chave...");
                        apiKeyManager.markKeyExhausted(activeKey);
                        break;
                    }

                    throw new AiProviderException("Erro do provedor Gemini (HTTP " + e.getStatusCode() + "): " + e.getResponseBodyAsString(), e);
                } catch (Exception e) {
                    lastException = e;
                    log.error("Erro inesperado na chamada ao modelo '{}': {}", modelName, e.getMessage());
                    if (isTemporaryServerError(503, e.getMessage())) {
                        log.warn("Modelo Gemini '{}' com indisponibilidade temporária. Alternando modelo...", modelName);
                        continue;
                    }
                    if (isQuotaError(e.getMessage())) {
                        apiKeyManager.markKeyExhausted(activeKey);
                        break;
                    }
                }
            }
        }

        throw new AiProviderException("Todas as tentativas de geração com os modelos e chaves configurados falharam.", lastException);
    }

    @Override
    public String generateWithImage(String prompt, String base64Image, String mimeType) {
        int maxKeyAttempts = Math.max(1, apiKeyManager.getAvailableKeysCount());
        List<String> candidateModels = getCandidateModels();
        Exception lastException = null;

        for (int keyAttempt = 1; keyAttempt <= maxKeyAttempts; keyAttempt++) {
            String activeKey = apiKeyManager.getActiveKey();

            for (String modelName : candidateModels) {
                try {
                    String url = GEMINI_BASE_URL + modelName + ":generateContent?key=" + activeKey;
                    log.debug("Chamando Gemini API multimodal com modelo '{}' (tentativa {}/{})", modelName, keyAttempt, maxKeyAttempts);

                    Map<String, Object> requestBody = Map.of(
                            "contents", List.of(
                                    Map.of("parts", List.of(
                                            Map.of("text", prompt),
                                            Map.of("inline_data", Map.of(
                                                    "mime_type", mimeType != null ? mimeType : "image/png",
                                                    "data", base64Image
                                            ))
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

                    if (e.getStatusCode().value() == 404) {
                        log.info("Modelo Gemini '{}' não encontrado para esta API Key (HTTP 404). Tentando modelo alternativo...", modelName);
                        continue;
                    }

                    if (isTemporaryServerError(e.getStatusCode().value(), e.getResponseBodyAsString())) {
                        log.warn("Modelo Gemini multimodal '{}' temporariamente sobrecarregado (HTTP {}). Alternando modelo candidato...", modelName, e.getStatusCode().value());
                        continue;
                    }

                    if (e.getStatusCode().value() == 429 || isQuotaError(e.getResponseBodyAsString())) {
                        log.warn("Limite de cota atingido no modelo '{}' (HTTP 429). Tentando próximo modelo candidato...", modelName);
                        continue;
                    }

                    throw new AiProviderException("Erro do provedor Gemini (HTTP " + e.getStatusCode() + "): " + e.getResponseBodyAsString(), e);
                } catch (Exception e) {
                    lastException = e;
                    log.error("Erro inesperado na chamada multimodal ao modelo '{}': {}", modelName, e.getMessage());
                    if (isTemporaryServerError(503, e.getMessage())) {
                        log.warn("Modelo multimodal '{}' com indisponibilidade temporária. Alternando modelo...", modelName);
                        continue;
                    }
                    if (isQuotaError(e.getMessage())) {
                        apiKeyManager.markKeyExhausted(activeKey);
                        break;
                    }
                }
            }
        }

        throw new AiProviderException("Todas as tentativas de geração multimodal falharam.", lastException);
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

    private List<String> getCandidateModels() {
        List<String> models = new ArrayList<>();
        if (configuredModel != null && !configuredModel.isBlank()) {
            models.add(configuredModel.trim());
        }
        for (String fallback : FALLBACK_MODELS) {
            if (!models.contains(fallback)) {
                models.add(fallback);
            }
        }
        return models;
    }

    private boolean isQuotaError(String errorDetails) {
        if (errorDetails == null) return false;
        String lower = errorDetails.toLowerCase();
        return lower.contains("quota") || lower.contains("resource_exhausted") || lower.contains("rate limit") || lower.contains("429");
    }

    private boolean isTemporaryServerError(int statusCode, String errorDetails) {
        if (statusCode >= 500 && statusCode <= 599) {
            return true;
        }
        if (errorDetails != null) {
            String lower = errorDetails.toLowerCase();
            return lower.contains("503") || lower.contains("service_unavailable") || lower.contains("unavailable") || lower.contains("high demand") || lower.contains("overloaded");
        }
        return false;
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
