package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IdiomaTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Idioma idioma = Idioma.builder()
                .nombre("Español")
                .build();

        assertNoViolations(idioma);
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void shouldFailWhenNameIsBlank() {
        Idioma idioma = Idioma.builder()
                .nombre("")
                .build();

        assertHasViolation(idioma, "nombre", "El nombre no puede estar vacio");
    }

    @Test
    @DisplayName("Should fail when name exceeds maximum length")
    void shouldFailWhenNameTooLong() {
        Idioma idioma = Idioma.builder()
                .nombre("a".repeat(51))
                .build();

        assertHasViolation(idioma, "nombre", "El nombre no puede ser mayor de 50 caracteres");
    }
}
