package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MuebleTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Mueble mueble = Mueble.builder()
                .nombre("Estante 1")
                .salon(new Salon())
                .build();

        assertNoViolations(mueble);
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void shouldFailWhenNameIsBlank() {
        Mueble mueble = Mueble.builder()
                .nombre("")
                .build();

        assertHasViolation(mueble, "nombre", "El nombre del mueble no puede estar vacío");
    }

    @Test
    @DisplayName("Should fail when name too long")
    void shouldFailWhenNameTooLong() {
        Mueble mueble = Mueble.builder()
                .nombre("a".repeat(51))
                .build();

        assertHasViolation(mueble, "nombre", "El nombre no puede exceder 50 caracteres");
    }
}
