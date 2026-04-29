package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NacionalidadTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Nacionalidad nacionalidad = Nacionalidad.builder()
                .nombre("Salvadoreña")
                .build();

        assertNoViolations(nacionalidad);
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void shouldFailWhenNameIsBlank() {
        Nacionalidad nacionalidad = Nacionalidad.builder()
                .nombre("")
                .build();

        assertHasViolation(nacionalidad, "nombre", "El nombre no puede estar vacio");
    }

    @Test
    @DisplayName("Should fail when name exceeds maximum length")
    void shouldFailWhenNameTooLong() {
        Nacionalidad nacionalidad = Nacionalidad.builder()
                .nombre("a".repeat(51))
                .build();

        assertHasViolation(nacionalidad, "nombre", "El nombreno puede exceder los 50 caracteres");
    }
}
