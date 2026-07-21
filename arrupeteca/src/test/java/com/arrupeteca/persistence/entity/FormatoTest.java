package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Formato Entity Unit Tests")
class FormatoTest {

    @Test
    @DisplayName("Should test getters and setters")
    void shouldTestGettersAndSetters() {
        Formato formato = Formato.builder()
                .id(1L)
                .nombre("Impreso")
                .borradoLogico(true)
                .build();

        assertThat(formato.getId()).isEqualTo(1L);
        assertThat(formato.getNombre()).isEqualTo("Impreso");
        assertThat(formato.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsHashCode() {
        Formato f1 = Formato.builder().id(1L).build();
        Formato f2 = Formato.builder().id(1L).build();
        Formato f3 = Formato.builder().id(2L).build();

        assertThat(f1).isEqualTo(f1);
        assertThat(f1).isEqualTo(f2);
        assertThat(f1).isNotEqualTo(f3);
        assertThat(f1).isNotEqualTo(null);
        assertThat(f1).isNotEqualTo(new Object());
        assertThat(f1.hashCode()).isEqualTo(f2.hashCode());
    }
}
