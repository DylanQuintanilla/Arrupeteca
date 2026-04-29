package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TipoAutoriaTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        TipoAutoria tipo = TipoAutoria.builder()
                .nombre("Autor Principal")
                .build();

        assertNoViolations(tipo);
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void shouldFailWhenNameIsBlank() {
        TipoAutoria tipo = TipoAutoria.builder()
                .nombre("")
                .build();

        assertHasViolation(tipo, "nombre", "El nombre no puede estar vacío");
    }

    @Test
    @DisplayName("Should fail when name exceeds maximum length")
    void shouldFailWhenNameTooLong() {
        TipoAutoria tipo = TipoAutoria.builder()
                .nombre("a".repeat(26))
                .build();

        assertHasViolation(tipo, "nombre", "El nombre no puede exceder 25 caracteres");
    }
}
