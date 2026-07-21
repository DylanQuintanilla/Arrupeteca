package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TipoAutoria Entity Unit Tests")
class TipoAutoriaTest {

    @Test
    @DisplayName("Should test getters and setters")
    void shouldTestGettersAndSetters() {
        TipoAutoria tipoAutoria = TipoAutoria.builder()
                .id(1L)
                .nombre("Autor Principal")
                .borradoLogico(true)
                .build();

        assertThat(tipoAutoria.getId()).isEqualTo(1L);
        assertThat(tipoAutoria.getNombre()).isEqualTo("Autor Principal");
        assertThat(tipoAutoria.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsHashCode() {
        TipoAutoria t1 = TipoAutoria.builder().id(1L).build();
        TipoAutoria t2 = TipoAutoria.builder().id(1L).build();
        TipoAutoria t3 = TipoAutoria.builder().id(2L).build();

        assertThat(t1).isEqualTo(t1);
        assertThat(t1).isEqualTo(t2);
        assertThat(t1).isNotEqualTo(t3);
        assertThat(t1).isNotEqualTo(null);
        assertThat(t1).isNotEqualTo(new Object());
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    }
}
