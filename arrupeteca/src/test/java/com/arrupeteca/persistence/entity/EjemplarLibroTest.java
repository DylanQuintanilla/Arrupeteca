package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EjemplarLibro Entity Unit Tests")
class EjemplarLibroTest {

    @Test
    @DisplayName("Should test getters, setters and builder")
    void shouldTestGettersSettersAndBuilder() {
        EstadoFisico ef = EstadoFisico.builder().id(1L).nombre("Buen estado").build();
        Disponibilidad disp = Disponibilidad.builder().id(1L).nombre("Disponible").build();

        EjemplarLibro ejemplar = EjemplarLibro.builder()
                .id(1L)
                .comentario("Buen estado")
                .libro(Libro.builder().build())
                .mueble(Mueble.builder().build())
                .estadoFisico(ef)
                .disponibilidad(disp)
                .borradoLogico(true)
                .build();

        assertThat(ejemplar.getId()).isEqualTo(1L);
        assertThat(ejemplar.getComentario()).isEqualTo("Buen estado");
        assertThat(ejemplar.getLibro()).isNotNull();
        assertThat(ejemplar.getMueble()).isNotNull();
        assertThat(ejemplar.getEstadoFisico()).isEqualTo(ef);
        assertThat(ejemplar.getDisponibilidad()).isEqualTo(disp);
        assertThat(ejemplar.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsAndHashCode() {
        EjemplarLibro e1 = EjemplarLibro.builder().id(1L).build();
        EjemplarLibro e2 = EjemplarLibro.builder().id(1L).build();
        EjemplarLibro e3 = EjemplarLibro.builder().id(2L).build();

        assertThat(e1).isEqualTo(e1);
        assertThat(e1).isEqualTo(e2);
        assertThat(e1).isNotEqualTo(e3);
        assertThat(e1).isNotEqualTo(null);
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
    }
}
