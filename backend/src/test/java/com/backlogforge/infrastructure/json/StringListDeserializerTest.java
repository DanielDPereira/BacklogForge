package com.backlogforge.infrastructure.json;

import com.backlogforge.domain.ProductBacklog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringListDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Deve desserializar um JSON Array de Strings padrão")
    void shouldDeserializeStandardJsonArray() throws Exception {
        String json = """
                {
                    "projectName": "Teste",
                    "summary": "Resumo",
                    "suggestedTechnologies": ["Java 21", "Spring Boot", "React"],
                    "epics": [],
                    "sprints": []
                }
                """;

        ProductBacklog backlog = objectMapper.readValue(json, ProductBacklog.class);

        assertNotNull(backlog);
        assertEquals(3, backlog.suggestedTechnologies().size());
        assertTrue(backlog.suggestedTechnologies().contains("Java 21"));
        assertTrue(backlog.suggestedTechnologies().contains("Spring Boot"));
        assertTrue(backlog.suggestedTechnologies().contains("React"));
    }

    @Test
    @DisplayName("Deve desserializar resiliente quando a IA retornar um JSON Object em vez de Array")
    void shouldDeserializeJsonObjectResiliently() throws Exception {
        String json = """
                {
                    "projectName": "Teste",
                    "summary": "Resumo",
                    "suggestedTechnologies": {
                        "frontend": "React",
                        "backend": "Spring Boot",
                        "database": "PostgreSQL"
                    },
                    "epics": [],
                    "sprints": []
                }
                """;

        ProductBacklog backlog = objectMapper.readValue(json, ProductBacklog.class);

        assertNotNull(backlog);
        assertEquals(3, backlog.suggestedTechnologies().size());
        assertTrue(backlog.suggestedTechnologies().contains("React"));
        assertTrue(backlog.suggestedTechnologies().contains("Spring Boot"));
        assertTrue(backlog.suggestedTechnologies().contains("PostgreSQL"));
    }

    @Test
    @DisplayName("Deve desserializar resiliente quando a IA retornar uma String delimitada por vírgula")
    void shouldDeserializeCommaSeparatedString() throws Exception {
        String json = """
                {
                    "projectName": "Teste",
                    "summary": "Resumo",
                    "suggestedTechnologies": "Java 21, Spring Boot, Docker",
                    "epics": [],
                    "sprints": []
                }
                """;

        ProductBacklog backlog = objectMapper.readValue(json, ProductBacklog.class);

        assertNotNull(backlog);
        assertEquals(3, backlog.suggestedTechnologies().size());
        assertTrue(backlog.suggestedTechnologies().contains("Java 21"));
        assertTrue(backlog.suggestedTechnologies().contains("Spring Boot"));
        assertTrue(backlog.suggestedTechnologies().contains("Docker"));
    }

    @Test
    @DisplayName("Deve desserializar objeto aninhado com arrays sem falhar")
    void shouldDeserializeNestedObjectWithArrays() throws Exception {
        String json = """
                {
                    "projectName": "Teste",
                    "summary": "Resumo",
                    "suggestedTechnologies": {
                        "base": ["Java 21", "Spring Boot"],
                        "extra": ["Docker", "Kubernetes"]
                    },
                    "epics": [],
                    "sprints": []
                }
                """;

        ProductBacklog backlog = objectMapper.readValue(json, ProductBacklog.class);

        assertNotNull(backlog);
        assertEquals(4, backlog.suggestedTechnologies().size());
        assertTrue(backlog.suggestedTechnologies().contains("Java 21"));
        assertTrue(backlog.suggestedTechnologies().contains("Kubernetes"));
    }
}
