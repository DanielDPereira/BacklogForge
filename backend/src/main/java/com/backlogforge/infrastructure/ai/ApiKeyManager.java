package com.backlogforge.infrastructure.ai;

import com.backlogforge.domain.exception.AiProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gerenciador thread-safe de chaves da API do Gemini.
 * Oferece rotação automática de credenciais e cooldown de chaves que atingiram limite de quota (HTTP 429).
 */
@Component
public class ApiKeyManager {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyManager.class);
    private static final long COOLDOWN_SECONDS = 300; // 5 minutos de pausa para chave esgotada

    private final List<String> apiKeys = new ArrayList<>();
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private final Map<String, Instant> disabledKeys = new ConcurrentHashMap<>();

    public ApiKeyManager(
            @Value("${spring.ai.gemini.api-keys:${GEMINI_API_KEYS:}}") String multiKeys,
            @Value("${spring.ai.gemini.api-key:${GEMINI_API_KEY:}}") String singleKey
    ) {
        parseAndAddKeys(multiKeys);
        parseAndAddKeys(singleKey);

        if (apiKeys.isEmpty()) {
            log.warn("Nenhuma chave de API do Gemini foi configurada em GEMINI_API_KEY ou GEMINI_API_KEYS.");
        } else {
            log.info("ApiKeyManager inicializado com {} chave(s) de API cadastradas.", apiKeys.size());
        }
    }

    private void parseAndAddKeys(String rawKeys) {
        if (rawKeys != null && !rawKeys.isBlank()) {
            Arrays.stream(rawKeys.split(","))
                    .map(String::trim)
                    .filter(k -> !k.isBlank())
                    .filter(k -> !apiKeys.contains(k))
                    .forEach(apiKeys::add);
        }
    }

    public synchronized String getActiveKey() {
        if (apiKeys.isEmpty()) {
            throw new AiProviderException("A chave da API do Gemini (GEMINI_API_KEY) não foi configurada no ambiente.");
        }

        int totalKeys = apiKeys.size();
        Instant now = Instant.now();

        for (int i = 0; i < totalKeys; i++) {
            int index = (currentIndex.get() + i) % totalKeys;
            String key = apiKeys.get(index);

            Instant cooldownUntil = disabledKeys.get(key);
            if (cooldownUntil != null) {
                if (now.isAfter(cooldownUntil)) {
                    disabledKeys.remove(key);
                    log.info("Chave de API reativada após término do período de cooldown (final do sulfixo ...{})", maskKey(key));
                    currentIndex.set(index);
                    return key;
                } else {
                    log.debug("Chave ...{} ignorada por estar em cooldown até {}", maskKey(key), cooldownUntil);
                    continue;
                }
            }

            currentIndex.set(index);
            return key;
        }

        throw new AiProviderException("Todas as " + totalKeys + " chaves de API do Gemini configuradas estão temporariamente esgotadas por limite de quota (HTTP 429).");
    }

    public synchronized void markKeyExhausted(String key) {
        if (key != null && apiKeys.contains(key)) {
            disabledKeys.put(key, Instant.now().plusSeconds(COOLDOWN_SECONDS));
            log.warn("Chave de API ...{} marcada como esgotada por limite de quota (cooldown de 5 min).", maskKey(key));
            
            // Avança o índice para a próxima chave
            currentIndex.incrementAndGet();
        }
    }

    public int getAvailableKeysCount() {
        return apiKeys.size();
    }

    private String maskKey(String key) {
        if (key == null || key.length() <= 6) {
            return "***";
        }
        return key.substring(key.length() - 6);
    }
}
