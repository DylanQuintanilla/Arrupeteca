package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Salon Entity Unit Tests")
class SalonTest {

    @Test
    @DisplayName("Should test getters and setters")
    void shouldTestGettersAndSetters() {
        Planta planta = Planta.builder().id(1L).nombre("Planta 1").build();
        Salon salon = Salon.builder()
                .id(1L)
                .nombre("Salón A")
                .planta(planta)
                .borradoLogico(true)
                .build();

        assertThat(salon.getId()).isEqualTo(1L);
        assertThat(salon.getNombre()).isEqualTo("Salón A");
        assertThat(salon.getPlanta()).isEqualTo(planta);
        assertThat(salon.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsHashCode() {
        Salon s1 = Salon.builder().id(1L).build();
        Salon s2 = Salon.builder().id(1L).build();
        Salon s3 = Salon.builder().id(2L).build();

        assertThat(s1).isEqualTo(s1);
        assertThat(s1).isEqualTo(s2);
        assertThat(s1).isNotEqualTo(s3);
        assertThat(s1).isNotEqualTo(null);
        assertThat(s1).isNotEqualTo(new Object());
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
    }
}
