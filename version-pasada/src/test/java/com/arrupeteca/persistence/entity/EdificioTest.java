package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EdificioTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Edificio edificio = Edificio.builder()
                .nombre("Edificio Central")
                .build();

        assertNoViolations(edificio);
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void shouldFailWhenNameIsBlank() {
        Edificio edificio = Edificio.builder()
                .nombre("")
                .build();

        assertHasViolation(edificio, "nombre", "El nombre del edificio no puede estar vacío");
    }

    @Test
    @DisplayName("Should fail when name exceeds maximum length")
    void shouldFailWhenNameTooLong() {
        Edificio edificio = Edificio.builder()
                .nombre("a".repeat(51))
                .build();

        assertHasViolation(edificio, "nombre", "El nombre no puede exceder 50 caracteres");
    }
}
