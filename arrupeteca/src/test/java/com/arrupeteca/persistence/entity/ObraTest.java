package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Obra Entity Unit Tests")
class ObraTest {

    @Test
    @DisplayName("Should test getters, setters and builder")
    void shouldTestGettersSettersAndBuilder() {
        Obra obra = Obra.builder()
                .id(1L)
                .titulo("Don Quijote de la Mancha")
                .resumen("Novela clásica...")
                .anioCreacion(1605)
                .borradoLogico(true)
                .build();

        assertThat(obra.getId()).isEqualTo(1L);
        assertThat(obra.getTitulo()).isEqualTo("Don Quijote de la Mancha");
        assertThat(obra.getResumen()).isEqualTo("Novela clásica...");
        assertThat(obra.getAnioCreacion()).isEqualTo(1605);
        assertThat(obra.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsAndHashCode() {
        Obra o1 = Obra.builder().id(1L).build();
        Obra o2 = Obra.builder().id(1L).build();
        Obra o3 = Obra.builder().id(2L).build();

        assertThat(o1).isEqualTo(o1);
        assertThat(o1).isEqualTo(o2);
        assertThat(o1).isNotEqualTo(o3);
        assertThat(o1).isNotEqualTo(null);
        assertThat(o1.hashCode()).isEqualTo(o2.hashCode());
    }
}
