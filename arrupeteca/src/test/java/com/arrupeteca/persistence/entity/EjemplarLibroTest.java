package com.arrupeteca.persistence.entity;

import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EjemplarLibroTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        EjemplarLibro ejemplar = EjemplarLibro.builder()
                .comentario("Buen estado")
                .libro(new Libro())
                .mueble(new Mueble())
                .estadoFisico(EstadoFisico.BUEN_ESTADO)
                .disponibilidad(Disponibilidad.DISPONIBLE)
                .build();

        assertNoViolations(ejemplar);
    }

    @Test
    @DisplayName("Should fail when comentario too long")
    void shouldFailWhenComentarioTooLong() {
        EjemplarLibro ejemplar = EjemplarLibro.builder()
                .comentario("a".repeat(256))
                .libro(new Libro())
                .mueble(new Mueble())
                .estadoFisico(EstadoFisico.BUEN_ESTADO)
                .disponibilidad(Disponibilidad.DISPONIBLE)
                .build();

        assertHasViolation(ejemplar, "comentario", "El comentario no puede exceder los 255 caracteres");
    }
}
