package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Nacionalidad Entity Unit Tests")
class NacionalidadTest {

    @Test
    @DisplayName("Should test getters and setters")
    void shouldTestGettersAndSetters() {
        Nacionalidad nacionalidad = Nacionalidad.builder()
                .id(1L)
                .nombre("Salvadoreña")
                .borradoLogico(true)
                .build();

        assertThat(nacionalidad.getId()).isEqualTo(1L);
        assertThat(nacionalidad.getNombre()).isEqualTo("Salvadoreña");
        assertThat(nacionalidad.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsHashCode() {
        Nacionalidad n1 = Nacionalidad.builder().id(1L).build();
        Nacionalidad n2 = Nacionalidad.builder().id(1L).build();
        Nacionalidad n3 = Nacionalidad.builder().id(2L).build();

        assertThat(n1).isEqualTo(n1);
        assertThat(n1).isEqualTo(n2);
        assertThat(n1).isNotEqualTo(n3);
        assertThat(n1).isNotEqualTo(null);
        assertThat(n1).isNotEqualTo(new Object());
        assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
    }
}
