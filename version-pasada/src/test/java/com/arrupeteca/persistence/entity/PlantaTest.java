package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlantaTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Planta planta = Planta.builder()
                .nombre("Planta 1")
                .edificio(new Edificio())
                .build();

        assertNoViolations(planta);
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void shouldFailWhenNameIsBlank() {
        Planta planta = Planta.builder()
                .nombre("")
                .build();

        assertHasViolation(planta, "nombre", "El nombre de la planta no puede estar vacío");
    }

    @Test
    @DisplayName("Should fail when name too long")
    void shouldFailWhenNameTooLong() {
        Planta planta = Planta.builder()
                .nombre("a".repeat(31))
                .build();

        assertHasViolation(planta, "nombre", "El nombre no puede exceder 30 caracteres");
    }
}
