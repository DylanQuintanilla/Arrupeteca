package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Idioma Entity Unit Tests")
class IdiomaTest {

    @Test
    @DisplayName("Should test getters and setters")
    void shouldTestGettersAndSetters() {
        Idioma idioma = Idioma.builder()
                .id(1L)
                .nombre("Español")
                .borradoLogico(true)
                .build();

        assertThat(idioma.getId()).isEqualTo(1L);
        assertThat(idioma.getNombre()).isEqualTo("Español");
        assertThat(idioma.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsHashCode() {
        Idioma i1 = Idioma.builder().id(1L).build();
        Idioma i2 = Idioma.builder().id(1L).build();
        Idioma i3 = Idioma.builder().id(2L).build();

        assertThat(i1).isEqualTo(i1);
        assertThat(i1).isEqualTo(i2);
        assertThat(i1).isNotEqualTo(i3);
        assertThat(i1).isNotEqualTo(null);
        assertThat(i1).isNotEqualTo(new Object());
        assertThat(i1.hashCode()).isEqualTo(i2.hashCode());
    }
}
