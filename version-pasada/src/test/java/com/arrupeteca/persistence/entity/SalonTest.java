package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SalonTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Salon salon = Salon.builder()
                .nombre("Salón A")
                .planta(new Planta())
                .build();

        assertNoViolations(salon);
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void shouldFailWhenNameIsBlank() {
        Salon salon = Salon.builder()
                .nombre("")
                .build();

        assertHasViolation(salon, "nombre", "El nombre del salon no puede estar vacío");
    }

    @Test
    @DisplayName("Should fail when name too long")
    void shouldFailWhenNameTooLong() {
        Salon salon = Salon.builder()
                .nombre("a".repeat(26))
                .build();

        assertHasViolation(salon, "nombre", "El nombre no puede exceder 25 caracteres");
    }
}
