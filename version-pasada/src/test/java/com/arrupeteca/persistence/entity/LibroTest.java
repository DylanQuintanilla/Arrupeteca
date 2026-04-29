package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

class LibroTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Libro libro = Libro.builder()
                .isbn("9780306406157")
                .anioPublicacion(2020)
                .obra(new Obra())
                .editorial(new Editorial())
                .formato(new Formato())
                .idiomas(Set.of(new Idioma()))
                .build();

        assertNoViolations(libro);
    }

    @Test
    @DisplayName("Should fail when ISBN too long")
    void shouldFailWhenIsbnTooLong() {
        Libro libro = Libro.builder()
                .isbn("1".repeat(21))
                .anioPublicacion(2020)
                .obra(new Obra())
                .editorial(new Editorial())
                .formato(new Formato())
                .idiomas(Set.of(new Idioma()))
                .build();

        assertHasViolation(libro, "isbn", "El ISBN no puede contener mas de 20 digitos");
    }
}
