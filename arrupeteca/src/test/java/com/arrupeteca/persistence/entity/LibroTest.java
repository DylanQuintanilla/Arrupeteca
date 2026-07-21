package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Libro Entity Unit Tests")
class LibroTest {

    @Test
    @DisplayName("Should test getters, setters and builder")
    void shouldTestGettersSettersAndBuilder() {
        Libro libro = Libro.builder()
                .id(1L)
                .isbn("9780306406157")
                .anioPublicacion(2020)
                .precio(BigDecimal.TEN)
                .mora(BigDecimal.ONE)
                .borradoLogico(true)
                .build();

        assertThat(libro.getId()).isEqualTo(1L);
        assertThat(libro.getIsbn()).isEqualTo("9780306406157");
        assertThat(libro.getAnioPublicacion()).isEqualTo(2020);
        assertThat(libro.getPrecio()).isEqualTo(BigDecimal.TEN);
        assertThat(libro.getMora()).isEqualTo(BigDecimal.ONE);
        assertThat(libro.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsAndHashCode() {
        Libro l1 = Libro.builder().id(1L).build();
        Libro l2 = Libro.builder().id(1L).build();
        Libro l3 = Libro.builder().id(2L).build();

        assertThat(l1).isEqualTo(l1);
        assertThat(l1).isEqualTo(l2);
        assertThat(l1).isNotEqualTo(l3);
        assertThat(l1).isNotEqualTo(null);
        assertThat(l1.hashCode()).isEqualTo(l2.hashCode());
    }
}
