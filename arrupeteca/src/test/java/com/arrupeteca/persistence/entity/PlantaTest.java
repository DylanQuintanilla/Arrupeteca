package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Planta Entity Unit Tests")
class PlantaTest {

    @Test
    @DisplayName("Should test getters and setters")
    void shouldTestGettersAndSetters() {
        Edificio edificio = Edificio.builder().id(1L).nombre("Edificio Central").build();
        Planta planta = Planta.builder()
                .id(1L)
                .nombre("Planta Baja")
                .edificio(edificio)
                .borradoLogico(true)
                .build();

        assertThat(planta.getId()).isEqualTo(1L);
        assertThat(planta.getNombre()).isEqualTo("Planta Baja");
        assertThat(planta.getEdificio()).isEqualTo(edificio);
        assertThat(planta.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsHashCode() {
        Planta p1 = Planta.builder().id(1L).build();
        Planta p2 = Planta.builder().id(1L).build();
        Planta p3 = Planta.builder().id(2L).build();

        assertThat(p1).isEqualTo(p1);
        assertThat(p1).isEqualTo(p2);
        assertThat(p1).isNotEqualTo(p3);
        assertThat(p1).isNotEqualTo(null);
        assertThat(p1).isNotEqualTo(new Object());
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }
}
