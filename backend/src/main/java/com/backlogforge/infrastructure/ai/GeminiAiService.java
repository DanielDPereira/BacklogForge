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
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Implementação do serviço de IA integrado com a API do Google Gemini.
 * Suporta configuração via chave de API e resposta estruturada em JSON.
 */
@Service
public class GeminiAiService implements AiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiService.class);
    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public GeminiAiService(
            ObjectMapper objectMapper,
            @Value("${spring.ai.gemini.api-key:${GEMINI_API_KEY:}}") String apiKey
    ) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    @Override
    public String generate(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Chave de API do Gemini não configurada (GEMINI_API_KEY).");
            throw new AiProviderException("A chave da API do Gemini (GEMINI_API_KEY) não foi configurada no ambiente.");
        }

        try {
            String url = GEMINI_ENDPOINT + "?key=" + apiKey;

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

            throw new AiProviderException("Falha na resposta do Gemini. HTTP Status: " + response.getStatusCode());
        } catch (Exception e) {
            log.error("Erro de comunicação com o Gemini API: {}", e.getMessage(), e);
            throw new AiProviderException("Erro ao se comunicar com o provedor Gemini: " + e.getMessage(), e);
        }
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
