package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Genero Entity Unit Tests")
class GeneroTest {

    @Test
    @DisplayName("Should test getters and setters")
    void shouldTestGettersAndSetters() {
        Genero genero = Genero.builder()
                .id(1L)
                .nombre("Terror")
                .borradoLogico(true)
                .build();

        assertThat(genero.getId()).isEqualTo(1L);
        assertThat(genero.getNombre()).isEqualTo("Terror");
        assertThat(genero.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsHashCode() {
        Genero g1 = Genero.builder().id(1L).build();
        Genero g2 = Genero.builder().id(1L).build();
        Genero g3 = Genero.builder().id(2L).build();

        assertThat(g1).isEqualTo(g1);
        assertThat(g1).isEqualTo(g2);
        assertThat(g1).isNotEqualTo(g3);
        assertThat(g1).isNotEqualTo(null);
        assertThat(g1).isNotEqualTo(new Object());
        assertThat(g1.hashCode()).isEqualTo(g2.hashCode());
    }
}
