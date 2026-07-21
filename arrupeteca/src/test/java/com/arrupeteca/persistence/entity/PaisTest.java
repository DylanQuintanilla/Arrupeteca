package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pais Entity Unit Tests")
class PaisTest {

    @Test
    @DisplayName("Should test getters and setters")
    void shouldTestGettersAndSetters() {
        Pais pais = Pais.builder()
                .id(1L)
                .nombre("El Salvador")
                .borradoLogico(true)
                .build();

        assertThat(pais.getId()).isEqualTo(1L);
        assertThat(pais.getNombre()).isEqualTo("El Salvador");
        assertThat(pais.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsHashCode() {
        Pais p1 = Pais.builder().id(1L).build();
        Pais p2 = Pais.builder().id(1L).build();
        Pais p3 = Pais.builder().id(2L).build();

        assertThat(p1).isEqualTo(p1);
        assertThat(p1).isEqualTo(p2);
        assertThat(p1).isNotEqualTo(p3);
        assertThat(p1).isNotEqualTo(null);
        assertThat(p1).isNotEqualTo(new Object());
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }
}
