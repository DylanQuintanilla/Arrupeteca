package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EditorialTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Editorial editorial = Editorial.builder()
                .nombre("Editorial Arrupe")
                .direccion("Calle Falsa 123")
                .correo("contacto@arrupe.com")
                .personaContacto("Juan Perez")
                .telefono("1234-5678")
                .build();

        assertNoViolations(editorial);
    }

    @Test
    @DisplayName("Should fail when required fields are blank")
    void shouldFailWhenRequiredFieldsAreBlank() {
        Editorial editorial = Editorial.builder()
                .nombre("")
                .correo("")
                .personaContacto("")
                .build();

        assertHasViolation(editorial, "nombre", "El nombre de la editorial no puede estar vacia");
        assertHasViolation(editorial, "correo", "El correo de la editorial no puede estar vacia");
        assertHasViolation(editorial, "personaContacto",
                "El nombre de la persona de contacto no puede estar vacia");
    }

    @Test
    @DisplayName("Should fail when email format is invalid")
    void shouldFailWhenEmailIsInvalid() {
        Editorial editorial = Editorial.builder()
                .nombre("Editorial")
                .correo("correo-invalido")
                .personaContacto("Contacto")
                .build();

        assertHasViolation(editorial, "correo", "El formato del correo es inválido");
    }

    @Test
    @DisplayName("Should fail when fields exceed maximum length")
    void shouldFailWhenFieldsTooLong() {
        Editorial editorial = Editorial.builder()
                .nombre("a".repeat(101))
                .direccion("a".repeat(151))
                .correo("a".repeat(90) + "@test.com")
                .personaContacto("a".repeat(101))
                .telefono("1234-5678")
                .build();

        assertHasViolation(editorial, "nombre", "El nombre de la editorial no debe exceder los 100 caracteres");
        assertHasViolation(editorial, "direccion",
                "La direccion de la editorial no debe exceder los 150 caracteres");
        editorial.setCorreo("a".repeat(101) + "@test.com");
        assertHasViolation(editorial, "correo", "El correo de la editorial no debe exceder los 100 caracteres");
        assertHasViolation(editorial, "personaContacto",
                "El nombre de la persona de contacto no debe exceder los 100 caracteres");
    }

}
