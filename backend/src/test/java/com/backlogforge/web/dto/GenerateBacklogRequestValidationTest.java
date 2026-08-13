package com.backlogforge.web.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GenerateBacklogRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve aceitar contrato válido de entrada")
    void shouldAcceptValidRequest() {
        GenerateBacklogRequest request = new GenerateBacklogRequest(
                "Sistema de Vendas",
                6,
                2,
                4,
                List.of("Java", "React"),
                true,
                "Contexto complementar"
        );

        Set<ConstraintViolation<GenerateBacklogRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Não deve haver violações para uma requisição válida");
    }

    @Test
    @DisplayName("Deve rejeitar nome do projeto em branco")
    void shouldRejectBlankProjectName() {
        GenerateBacklogRequest request = new GenerateBacklogRequest(
                "   ",
                6,
                2,
                4,
                List.of(),
                false,
                null
        );

        Set<ConstraintViolation<GenerateBacklogRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("projectName")));
    }

    @Test
    @DisplayName("Deve rejeitar número de sprints inválido (menor que 1 ou nulo)")
    void shouldRejectInvalidSprintCount() {
        GenerateBacklogRequest request = new GenerateBacklogRequest(
                "Projeto Teste",
                0,
                2,
                4,
                List.of(),
                false,
                null
        );

        Set<ConstraintViolation<GenerateBacklogRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sprintCount")));
    }
}
