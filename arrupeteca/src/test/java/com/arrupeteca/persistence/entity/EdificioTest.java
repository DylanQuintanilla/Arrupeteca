package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Edificio Entity Unit Tests")
class EdificioTest {

    @Test
    @DisplayName("Should test getters and setters")
    void shouldTestGettersAndSetters() {
        Edificio edificio = Edificio.builder()
                .id(1L)
                .nombre("Edificio Central")
                .borradoLogico(true)
                .build();

        assertThat(edificio.getId()).isEqualTo(1L);
        assertThat(edificio.getNombre()).isEqualTo("Edificio Central");
        assertThat(edificio.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsHashCode() {
        Edificio e1 = Edificio.builder().id(1L).build();
        Edificio e2 = Edificio.builder().id(1L).build();
        Edificio e3 = Edificio.builder().id(2L).build();

        assertThat(e1).isEqualTo(e1);
        assertThat(e1).isEqualTo(e2);
        assertThat(e1).isNotEqualTo(e3);
        assertThat(e1).isNotEqualTo(null);
        assertThat(e1).isNotEqualTo(new Object());
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
    }
}
