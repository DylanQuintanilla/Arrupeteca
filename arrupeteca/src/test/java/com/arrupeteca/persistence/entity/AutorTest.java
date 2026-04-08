package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AutorTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Autor autor = Autor.builder()
                .nombre("Miguel de Cervantes")
                .seudonimo("El Manco de Lepanto")
                .biografia("Escritor español...")
                .build();

        assertNoViolations(autor);
    }

    @Test
    @DisplayName("Should fail when name too long")
    void shouldFailWhenNameTooLong() {
        Autor autor = Autor.builder()
                .nombre("a".repeat(101))
                .biografia("Bio")
                .build();

        assertHasViolation(autor, "nombre", "El nombre no puede exceder 100 caracteres");
    }

    @Test
    @DisplayName("Should fail when seudonimo too long")
    void shouldFailWhenSeudonimoTooLong() {
        Autor autor = Autor.builder()
                .seudonimo("a".repeat(51))
                .biografia("Bio")
                .build();

        assertHasViolation(autor, "seudonimo", "El seudonimo no puede exceder 50 caracteres");
    }

    @Test
    @DisplayName("Should fail when biografia is blank")
    void shouldFailWhenBioIsBlank() {
        Autor autor = Autor.builder()
                .nombre("Autor")
                .biografia("")
                .build();

        assertHasViolation(autor, "biografia", "La biografia no puede estar vacía");
    }
}
