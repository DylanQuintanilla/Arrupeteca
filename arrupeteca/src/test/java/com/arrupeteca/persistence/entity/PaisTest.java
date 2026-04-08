package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaisTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Pais pais = Pais.builder()
                .nombre("El Salvador")
                .build();

        assertNoViolations(pais);
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void shouldFailWhenNameIsBlank() {
        Pais pais = Pais.builder()
                .nombre("")
                .build();

        assertHasViolation(pais, "nombre", "El nombre no puede estar vacio");
    }

    @Test
    @DisplayName("Should fail when name exceeds maximum length")
    void shouldFailWhenNameTooLong() {
        Pais pais = Pais.builder()
                .nombre("a".repeat(51))
                .build();

        assertHasViolation(pais, "nombre", "El nombre no puede exceder los 50 caracteres");
    }
}
