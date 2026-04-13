package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Autor Persistence Tests")
class AutorTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should validate identity correctly")
    void shouldValidateIdentity() {
        assertThat(Autor.builder().nombre("Nombre").build().isIdentidadValida()).isTrue();
        assertThat(Autor.builder().seudonimo("Seudonimo").build().isIdentidadValida()).isTrue();
        assertThat(Autor.builder().nombre("").seudonimo(null).build().isIdentidadValida()).isFalse();
        assertThat(Autor.builder().nombre(null).seudonimo(" ").build().isIdentidadValida()).isFalse();
    }

    @Test
    @DisplayName("Should validate dates correctly")
    void shouldValidateDates() {
        assertThat(Autor.builder().anioNacimiento(1900).anioFallecimiento(1980).build().isFechasValidas()).isTrue();
        assertThat(Autor.builder().anioNacimiento(1900).anioFallecimiento(1900).build().isFechasValidas()).isTrue();
        assertThat(Autor.builder().anioNacimiento(1980).anioFallecimiento(1900).build().isFechasValidas()).isFalse();
        assertThat(Autor.builder().anioNacimiento(1900).build().isFechasValidas()).isTrue();
        assertThat(Autor.builder().anioFallecimiento(1900).build().isFechasValidas()).isTrue();
    }

    @Test
    @DisplayName("Should test all getters and setters")
    void shouldTestGettersAndSetters() {
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNombre("Nombre");
        autor.setSeudonimo("Seudonimo");
        autor.setAnioNacimiento(1900);
        autor.setAnioFallecimiento(2000);
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

        assertThat(autor.getId()).isEqualTo(1L);
        assertThat(autor.getNombre()).isEqualTo("Nombre");
        assertThat(autor.getSeudonimo()).isEqualTo("Seudonimo");
        assertThat(autor.getAnioNacimiento()).isEqualTo(1900);
        assertThat(autor.getAnioFallecimiento()).isEqualTo(2000);
        assertThat(autor.getBiografia()).isEqualTo("Bio");
        assertThat(autor.isBorradoLogico()).isTrue();
        assertThat(autor.getPaisNacimiento()).isEqualTo(pais);
        assertThat(autor.getNacionalidad()).isEqualTo(nac);
        assertThat(autor.getGeneroPrincipal()).isEqualTo(gen);
        assertThat(autor.getObraCumbre()).isEqualTo(obra);
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

        assertThat(a1).isEqualTo(a2);
        assertThat(a1).isNotEqualTo(a3);
        assertThat(a1).isNotEqualTo(null);
        assertThat(a1).isNotEqualTo(new Object());
        assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
    }
}
