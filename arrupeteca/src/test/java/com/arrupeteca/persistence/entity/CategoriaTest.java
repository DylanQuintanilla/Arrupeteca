package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Categoria categoria = Categoria.builder()
                .nombre("Novela")
                .borradoLogico(false)
                .build();

        assertNoViolations(categoria);
    }

    @Test
    @DisplayName("Should fail when name is blank")
    void shouldFailWhenNameIsBlank() {
        Categoria categoria = Categoria.builder()
                .nombre("")
                .build();

        assertHasViolation(categoria, "nombre", "El nombre de la categoria no puede estar vacio");
    }

    @Test
    @DisplayName("Should fail when name exceeds maximum length")
    void shouldFailWhenNameTooLong() {
        Categoria categoria = Categoria.builder()
                .nombre("a".repeat(51))
                .build();

        assertHasViolation(categoria, "nombre", "El nombre de la categoria no puede exceder 50 caracteres");
    }
}
