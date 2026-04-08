package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormatoTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Formato formato = Formato.builder()
                .nombre("Tapa Dura")
                .build();

        assertNoViolations(formato);
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void shouldFailWhenNameIsBlank() {
        Formato formato = Formato.builder()
                .nombre("")
                .build();

        assertHasViolation(formato, "nombre", "El nombre del formato no puede ser vacio");
    }

    @Test
    @DisplayName("Should fail when name exceeds maximum length")
    void shouldFailWhenNameTooLong() {
        Formato formato = Formato.builder()
                .nombre("a".repeat(51))
                .build();

        assertHasViolation(formato, "nombre", "El nombre del formato no debe exceder los 50 caracteres");
    }
}
