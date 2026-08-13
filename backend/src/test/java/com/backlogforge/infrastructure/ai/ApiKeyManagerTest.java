package com.backlogforge.infrastructure.ai;

import com.backlogforge.domain.exception.AiProviderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyManagerTest {

    @Test
    @DisplayName("Deve inicializar com múltiplas chaves e fazer rotação corretamente")
    void shouldRotateKeysCorrectly() {
        ApiKeyManager manager = new ApiKeyManager("key1, key2, key3", null);

        assertEquals(3, manager.getAvailableKeysCount());
        assertEquals("key1", manager.getActiveKey());
    }

    @Test
    @DisplayName("Deve colocar chave em cooldown quando marcada como esgotada e usar a próxima")
    void shouldFallbackToNextKeyWhenExhausted() {
        ApiKeyManager manager = new ApiKeyManager("key1, key2", null);

        assertEquals("key1", manager.getActiveKey());
        manager.markKeyExhausted("key1");

        // Agora deve retornar key2
        assertEquals("key2", manager.getActiveKey());
    }

    @Test
    @DisplayName("Deve lançar exceção se todas as chaves estiverem em cooldown")
    void shouldThrowExceptionWhenAllKeysExhausted() {
        ApiKeyManager manager = new ApiKeyManager("key1, key2", null);

        manager.markKeyExhausted("key1");
        manager.markKeyExhausted("key2");

        assertThrows(AiProviderException.class, manager::getActiveKey);
    }
}
