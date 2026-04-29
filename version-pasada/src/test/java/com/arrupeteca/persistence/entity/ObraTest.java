package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

class ObraTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Obra obra = Obra.builder()
                .titulo("Don Quijote de la Mancha")
                .resumen("Novela clásica...")
                .generos(Set.of(new Genero()))
                .categorias(Set.of(new Categoria()))
                .build();

        assertNoViolations(obra);
    }

    @Test
    @DisplayName("Should fail when titulo is blank")
    void shouldFailWhenTituloIsBlank() {
        Obra obra = Obra.builder()
                .titulo("")
                .resumen("Resumen")
                .generos(Set.of(new Genero()))
                .categorias(Set.of(new Categoria()))
                .build();

        assertHasViolation(obra, "titulo", "El titulo no puede estas vacio");
    }

    @Test
    @DisplayName("Should fail when titulo too long")
    void shouldFailWhenTituloTooLong() {
        Obra obra = Obra.builder()
                .titulo("a".repeat(101))
                .resumen("Resumen")
                .generos(Set.of(new Genero()))
                .categorias(Set.of(new Categoria()))
                .build();

        assertHasViolation(obra, "titulo", "El titulo debe tener un maximo de 100 caracteres");
    }
}
