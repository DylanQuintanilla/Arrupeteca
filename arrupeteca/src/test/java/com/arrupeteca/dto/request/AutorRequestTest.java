package com.arrupeteca.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AutorRequestTest {

    @Test
    @DisplayName("Test Lombok methods (Getters, Setters, ToString, Equals, HashCode, Builder)")
    void testLombokMethods() {
        AutorRequest request1 = AutorRequest.builder()
                .nombre("Nombre")
                .seudonimo("Seudonimo")
                .anioNacimiento(1900)
                .anioFallecimiento(1980)
                .biografia("Bio")
                .idPaisNacimiento(1L)
                .idNacionalidad(2L)
                .idGeneroPrincipal(3L)
                .idObraCumbre(4L)
                .build();

        AutorRequest request2 = new AutorRequest();
        request2.setNombre("Nombre");
        request2.setSeudonimo("Seudonimo");
        request2.setAnioNacimiento(1900);
        request2.setAnioFallecimiento(1980);
        request2.setBiografia("Bio");
        request2.setIdPaisNacimiento(1L);
        request2.setIdNacionalidad(2L);
        request2.setIdGeneroPrincipal(3L);
        request2.setIdObraCumbre(4L);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertEquals(request1.toString(), request2.toString());
        
        assertEquals("Nombre", request1.getNombre());
        assertEquals("Seudonimo", request1.getSeudonimo());
        assertEquals(1900, request1.getAnioNacimiento());
        assertEquals(1980, request1.getAnioFallecimiento());
        assertEquals("Bio", request1.getBiografia());
        assertEquals(1L, request1.getIdPaisNacimiento());
        assertEquals(2L, request1.getIdNacionalidad());
        assertEquals(3L, request1.getIdGeneroPrincipal());
        assertEquals(4L, request1.getIdObraCumbre());
    }

    @Test
    @DisplayName("Test equals and hashCode with different values")
    void testEqualsAndHashCode() {
        AutorRequest request1 = AutorRequest.builder().nombre("A").build();
        AutorRequest request2 = AutorRequest.builder().nombre("B").build();
        AutorRequest request3 = AutorRequest.builder().nombre("A").build();

        assertNotEquals(request1, request2);
        assertNotEquals(request1.hashCode(), request2.hashCode());
        assertEquals(request1, request3);
        assertEquals(request1.hashCode(), request3.hashCode());
        
        assertNotEquals(null, request1);
        assertNotEquals(request1, new Object());
    }

    @Test
    @DisplayName("Test isIdentidadValida branches")
    void testIsIdentidadValida() {
        AutorRequest request = new AutorRequest();

        // nombre present, seudonimo null -> true
        request.setNombre("Nombre");
        request.setSeudonimo(null);
        assertTrue(request.isIdentidadValida());

        // nombre null, seudonimo present -> true
        request.setNombre(null);
        request.setSeudonimo("Seudonimo");
        assertTrue(request.isIdentidadValida());

        // both present -> true
        request.setNombre("Nombre");
        request.setSeudonimo("Seudonimo");
        assertTrue(request.isIdentidadValida());

        // nombre blank, seudonimo blank -> false
        request.setNombre("");
        request.setSeudonimo("  ");
        assertFalse(request.isIdentidadValida());

        // both null -> false
        request.setNombre(null);
        request.setSeudonimo(null);
        assertFalse(request.isIdentidadValida());
    }

    @Test
    @DisplayName("Test isFechasValidas branches")
    void testIsFechasValidas() {
        AutorRequest request = new AutorRequest();

        // anioNacimiento null -> true
        request.setAnioNacimiento(null);
        request.setAnioFallecimiento(2000);
        assertTrue(request.isFechasValidas());

        // anioFallecimiento null -> true
        request.setAnioNacimiento(1900);
        request.setAnioFallecimiento(null);
        assertTrue(request.isFechasValidas());

        // both null -> true
        request.setAnioNacimiento(null);
        request.setAnioFallecimiento(null);
        assertTrue(request.isFechasValidas());

        // anioNacimiento <= anioFallecimiento -> true
        request.setAnioNacimiento(1900);
        request.setAnioFallecimiento(1980);
        assertTrue(request.isFechasValidas());

        request.setAnioNacimiento(1900);
        request.setAnioFallecimiento(1900);
        assertTrue(request.isFechasValidas());

        // anioNacimiento > anioFallecimiento -> false
        request.setAnioNacimiento(2000);
        request.setAnioFallecimiento(1900);
        assertFalse(request.isFechasValidas());
    }
}
