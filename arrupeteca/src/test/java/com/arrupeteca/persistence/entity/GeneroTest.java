package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GeneroTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Genero genero = Genero.builder()
                .nombre("Terror")
                .build();

        assertNoViolations(genero);
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void shouldFailWhenNameIsBlank() {
        Genero genero = Genero.builder()
                .nombre("")
                .build();

        assertHasViolation(genero, "nombre", "El nombre no puedo estar vacio");
    }

    @Test
    @DisplayName("Should fail when name exceeds maximum length")
    void shouldFailWhenNameTooLong() {
        Genero genero = Genero.builder()
                .nombre("a".repeat(51))
                .build();

        assertHasViolation(genero, "nombre", "El nombre no puede exceder los 50 caracteres");
    }
}
