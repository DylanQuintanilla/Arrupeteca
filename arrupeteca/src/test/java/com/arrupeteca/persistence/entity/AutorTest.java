package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AutorTest extends BaseEntityTest {

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidation() {
        Autor autor = Autor.builder()
                .nombre("Miguel de Cervantes")
                .seudonimo("El Manco de Lepanto")
                .biografia("Escritor español...")
                .build();

        assertNoViolations(autor);
    }

    @Test
    @DisplayName("Should fail when name too long")
    void shouldFailWhenNameTooLong() {
        Autor autor = Autor.builder()
                .nombre("a".repeat(101))
                .biografia("Bio")
                .build();

        assertHasViolation(autor, "nombre", "El nombre no puede exceder 100 caracteres");
    }

    @Test
    @DisplayName("Should fail when seudonimo too long")
    void shouldFailWhenSeudonimoTooLong() {
        Autor autor = Autor.builder()
                .seudonimo("a".repeat(51))
                .biografia("Bio")
                .build();

        assertHasViolation(autor, "seudonimo", "El seudonimo no puede exceder 50 caracteres");
    }

    @Test
    @DisplayName("Should fail when biografia is blank")
    void shouldFailWhenBioIsBlank() {
        Autor autor = Autor.builder()
                .nombre("Autor")
                .biografia("")
                .build();

        assertHasViolation(autor, "biografia", "La biografia no puede estar vacía");
    }

    @Test
    @DisplayName("Test Lombok methods (Getters, Setters, ToString, Equals, HashCode, Builder)")
    void testLombokMethods() {
        Pais pais = Pais.builder().id(1L).nombre("Pais").build();
        Nacionalidad nac = Nacionalidad.builder().id(1L).nombre("Nac").build();
        Genero gen = Genero.builder().id(1L).nombre("Gen").build();
        Obra obra = Obra.builder().id(1L).titulo("Obra").build();

        Autor autor1 = Autor.builder()
                .id(1L)
                .nombre("Nombre")
                .seudonimo("Seudonimo")
                .anioNacimiento(1900)
                .anioFallecimiento(1980)
                .biografia("Bio")
                .borradoLogico(true)
                .paisNacimiento(pais)
                .nacionalidad(nac)
                .generoPrincipal(gen)
                .obraCumbre(obra)
                .build();

        Autor autor2 = new Autor();
        autor2.setId(1L);
        autor2.setNombre("Nombre");
        autor2.setSeudonimo("Seudonimo");
        autor2.setAnioNacimiento(1900);
        autor2.setAnioFallecimiento(1980);
        autor2.setBiografia("Bio");
        autor2.setBorradoLogico(true);
        autor2.setPaisNacimiento(pais);
        autor2.setNacionalidad(nac);
        autor2.setGeneroPrincipal(gen);
        autor2.setObraCumbre(obra);

        assertEquals(autor1, autor2);
        assertEquals(autor1.hashCode(), autor2.hashCode());
        assertNotNull(autor1.toString());
        
        assertEquals(1L, autor1.getId());
        assertEquals("Nombre", autor1.getNombre());
        assertEquals("Seudonimo", autor1.getSeudonimo());
        assertEquals(1900, autor1.getAnioNacimiento());
        assertEquals(1980, autor1.getAnioFallecimiento());
        assertEquals("Bio", autor1.getBiografia());
        assertTrue(autor1.isBorradoLogico());
        assertEquals(pais, autor1.getPaisNacimiento());
        assertEquals(nac, autor1.getNacionalidad());
        assertEquals(gen, autor1.getGeneroPrincipal());
        assertEquals(obra, autor1.getObraCumbre());
    }

    @Test
    @DisplayName("Test Equals and HashCode")
    void testEqualsAndHashCode() {
        Autor autor1 = Autor.builder().id(1L).build();
        Autor autor2 = Autor.builder().id(1L).build();
        Autor autor3 = Autor.builder().id(2L).build();

        assertEquals(autor1, autor2);
        assertEquals(autor1.hashCode(), autor2.hashCode());
        assertNotEquals(autor1, autor3);
        assertNotEquals(autor1.hashCode(), autor3.hashCode());
        
        assertNotEquals(null, autor1);
        assertNotEquals(autor1, new Object());
    }

    @Test
    @DisplayName("Test isIdentidadValida branches")
    void testIsIdentidadValida() {
        Autor autor = new Autor();

        // nombre present, seudonimo null -> true
        autor.setNombre("Nombre");
        autor.setSeudonimo(null);
        assertTrue(autor.isIdentidadValida());

        // nombre null, seudonimo present -> true
        autor.setNombre(null);
        autor.setSeudonimo("Seudonimo");
        assertTrue(autor.isIdentidadValida());

        // both present -> true
        autor.setNombre("Nombre");
        autor.setSeudonimo("Seudonimo");
        assertTrue(autor.isIdentidadValida());

        // nombre blank, seudonimo blank -> false
        autor.setNombre("");
        autor.setSeudonimo("  ");
        assertFalse(autor.isIdentidadValida());

        // both null -> false
        autor.setNombre(null);
        autor.setSeudonimo(null);
        assertFalse(autor.isIdentidadValida());
    }

    @Test
    @DisplayName("Test isFechasValidas branches")
    void testIsFechasValidas() {
        Autor autor = new Autor();

        // anioNacimiento null -> true
        autor.setAnioNacimiento(null);
        autor.setAnioFallecimiento(2000);
        assertTrue(autor.isFechasValidas());

        // anioFallecimiento null -> true
        autor.setAnioNacimiento(1900);
        autor.setAnioFallecimiento(null);
        assertTrue(autor.isFechasValidas());

        // both null -> true
        autor.setAnioNacimiento(null);
        autor.setAnioFallecimiento(null);
        assertTrue(autor.isFechasValidas());

        // anioNacimiento <= anioFallecimiento -> true
        autor.setAnioNacimiento(1900);
        autor.setAnioFallecimiento(1980);
        assertTrue(autor.isFechasValidas());

        autor.setAnioNacimiento(1900);
        autor.setAnioFallecimiento(1900);
        assertTrue(autor.isFechasValidas());

        // anioNacimiento > anioFallecimiento -> false
        autor.setAnioNacimiento(2000);
        autor.setAnioFallecimiento(1900);
        assertFalse(autor.isFechasValidas());
    }
}
