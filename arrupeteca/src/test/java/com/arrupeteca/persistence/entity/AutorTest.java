package com.arrupeteca.persistence.entity;

import java.time.Year;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Autor Entity Unit Tests")
class AutorTest {

    @Test
    @DisplayName("Should test all getters and setters")
    void shouldTestGettersAndSetters() {
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNombre("Nombre");
        autor.setSeudonimo("Seudonimo");
        autor.setAnioNacimiento(Year.of(1900));
        autor.setAnioFallecimiento(Year.of(2000));
        autor.setBiografia("Bio");
        autor.setBorradoLogico(true);
        
        Pais pais = new Pais();
        autor.setPaisNacimiento(pais);
        
        Nacionalidad nac = new Nacionalidad();
        autor.setNacionalidad(nac);
        
        Genero gen = new Genero();
        autor.setGeneroPrincipal(gen);
        
        Obra obra = new Obra();
        autor.setObraCumbre(obra);

        // Test de propiedades heredadas de Auditable
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        autor.setCreadoPor("User");
        autor.setFechaCreacion(now);
        autor.setActualizadoPor("Admin");
        autor.setFechaActualizacion(now);

        assertThat(autor.getId()).isEqualTo(1L);
        assertThat(autor.getNombre()).isEqualTo("Nombre");
        assertThat(autor.getSeudonimo()).isEqualTo("Seudonimo");
        assertThat(autor.getAnioNacimiento()).isEqualTo(Year.of(1900));
        assertThat(autor.getAnioFallecimiento()).isEqualTo(Year.of(2000));
        assertThat(autor.getBiografia()).isEqualTo("Bio");
        assertThat(autor.isBorradoLogico()).isTrue();
        assertThat(autor.getPaisNacimiento()).isEqualTo(pais);
        assertThat(autor.getNacionalidad()).isEqualTo(nac);
        assertThat(autor.getGeneroPrincipal()).isEqualTo(gen);
        assertThat(autor.getObraCumbre()).isEqualTo(obra);

        assertThat(autor.getCreadoPor()).isEqualTo("User");
        assertThat(autor.getFechaCreacion()).isEqualTo(now);
        assertThat(autor.getActualizadoPor()).isEqualTo("Admin");
        assertThat(autor.getFechaActualizacion()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should test toString")
    void shouldTestToString() {
        Autor autor = Autor.builder().id(1L).nombre("Test").build();
        assertThat(autor.toString()).contains("id=1", "nombre=Test");
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsHashCode() {
        Autor a1 = Autor.builder().id(1L).build();
        Autor a2 = Autor.builder().id(1L).build();
        Autor a3 = Autor.builder().id(2L).build();
        Autor aNull1 = Autor.builder().id(null).build();
        Autor aNull2 = Autor.builder().id(null).build();

        // Test de reflexividad
        assertThat(a1).isEqualTo(a1);

        // Test de simetría y diferencias
        assertThat(a1).isEqualTo(a2);
        assertThat(a1).isNotEqualTo(a3);
        assertThat(a1).isNotEqualTo(null);
        assertThat(a1).isNotEqualTo(new Object());
        assertThat(a1.hashCode()).isEqualTo(a2.hashCode());

        // Test con IDs nulos
        assertThat(aNull1).isEqualTo(aNull2);
        assertThat(a1).isNotEqualTo(aNull1);
        assertThat(aNull1.hashCode()).isEqualTo(aNull2.hashCode());
    }
}
