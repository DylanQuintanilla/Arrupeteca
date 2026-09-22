package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Editorial Entity Unit Tests")
class EditorialTest {

    @Test
    @DisplayName("Should test getters and setters")
    void shouldTestGettersAndSetters() {
        Editorial editorial = Editorial.builder()
                .id(1L)
                .nombre("Santillana")
                .direccion("Calle 123")
                .correo("contacto@santillana.com")
                .telefono("2222-3333")
                .personaContacto("Juan Pérez")
                .borradoLogico(true)
                .build();

        assertThat(editorial.getId()).isEqualTo(1L);
        assertThat(editorial.getNombre()).isEqualTo("Santillana");
        assertThat(editorial.getDireccion()).isEqualTo("Calle 123");
        assertThat(editorial.getCorreo()).isEqualTo("contacto@santillana.com");
        assertThat(editorial.getTelefono()).isEqualTo("2222-3333");
        assertThat(editorial.getPersonaContacto()).isEqualTo("Juan Pérez");
        assertThat(editorial.isBorradoLogico()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsHashCode() {
        Editorial e1 = Editorial.builder().id(1L).build();
        Editorial e2 = Editorial.builder().id(1L).build();
        Editorial e3 = Editorial.builder().id(2L).build();

        assertThat(e1).isEqualTo(e1);
        assertThat(e1).isEqualTo(e2);
        assertThat(e1).isNotEqualTo(e3);
        assertThat(e1).isNotEqualTo(null);
        assertThat(e1).isNotEqualTo(new Object());
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
    }
}
