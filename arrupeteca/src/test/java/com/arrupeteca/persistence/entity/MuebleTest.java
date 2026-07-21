package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Mueble Entity Unit Tests")
class MuebleTest {

    @Test
    @DisplayName("Should test getters and setters")
    void shouldTestGettersAndSetters() {
        Salon salon = Salon.builder().id(1L).nombre("Salón A").build();
        Mueble mueble = Mueble.builder()
                .id(1L)
                .nombre("Estante 1")
                .salon(salon)
                .borradoLogico(true)
                .build();

        assertThat(mueble.getId()).isEqualTo(1L);
        assertThat(mueble.getNombre()).isEqualTo("Estante 1");
        assertThat(mueble.getSalon()).isEqualTo(salon);
        assertThat(mueble.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsHashCode() {
        Mueble m1 = Mueble.builder().id(1L).build();
        Mueble m2 = Mueble.builder().id(1L).build();
        Mueble m3 = Mueble.builder().id(2L).build();

        assertThat(m1).isEqualTo(m1);
        assertThat(m1).isEqualTo(m2);
        assertThat(m1).isNotEqualTo(m3);
        assertThat(m1).isNotEqualTo(null);
        assertThat(m1).isNotEqualTo(new Object());
        assertThat(m1.hashCode()).isEqualTo(m2.hashCode());
    }
}
