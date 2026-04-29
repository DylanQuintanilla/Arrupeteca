package com.arrupeteca.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import org.junit.jupiter.api.Test;
import java.util.Collections;

class ComprehensiveRequestTest {

    @Test
    void shouldCoverAutorRequest() {
        AutorRequest request = AutorRequest.builder()
                .nombre("Nombre")
                .seudonimo("Seudonimo")
                .anioNacimiento(1900)
                .anioFallecimiento(1980)
                .biografia("Bio")
                .idPaisNacimiento(1L)
                .idNacionalidad(1L)
                .idGeneroPrincipal(1L)
                .idObraCumbre(1L)
                .build();

        assertThat(request.isIdentidadValida()).isTrue();
        assertThat(request.isFechasValidas()).isTrue();

        request.setNombre(null);
        assertThat(request.isIdentidadValida()).isTrue(); // Has seudonimo

        request.setSeudonimo(null);
        assertThat(request.isIdentidadValida()).isFalse(); // No identity

        request.setAnioNacimiento(2000);
        request.setAnioFallecimiento(1900);
        assertThat(request.isFechasValidas()).isFalse();

        // Trigger Lombok methods
        assertThat(request.toString()).contains("AutorRequest");
        AutorRequest other = new AutorRequest();
        assertThat(request.equals(other)).isFalse();
        assertThat(request.hashCode()).isNotZero();
    }

    @Test
    void shouldCoverAllOtherRequests() {
        // Just instantiate and trigger Lombok code for other requests
        verifyRequest(new CategoriaRequest("Cat"));
        verifyRequest(new EdificioRequest("Edif"));
        
        EditorialRequest edit = EditorialRequest.builder()
                .nombre("N")
                .direccion("D")
                .correo("e@e.com")
                .telefono("1234-5678")
                .personaContacto("P")
                .build();
        verifyRequest(edit);

        verifyRequest(new EjemplarLibroRequest("C", EstadoFisico.BUEN_ESTADO, Disponibilidad.DISPONIBLE, 1L, 1L));
        verifyRequest(new FormatoRequest("F"));
        verifyRequest(new GeneroRequest("G"));
        verifyRequest(new IdiomaRequest("I"));
        
        LibroRequest libro = new LibroRequest();
        libro.setIsbn("123");
        libro.setAnioPublicacion(2021);
        libro.setIdObra(1L);
        libro.setIdFormato(1L);
        libro.setIdEditorial(1L);
        libro.setIdIdiomas(Collections.singleton(1L));
        verifyRequest(libro);

        verifyRequest(new MuebleRequest("M", 1L));
        verifyRequest(new NacionalidadRequest("N"));
        verifyRequest(new ObraAutorRequest(1L, 1L));
        
        ObraRequest obra = ObraRequest.builder()
                .titulo("T")
                .anioCreacion(2020)
                .resumen("R")
                .idGeneros(Collections.singleton(1L))
                .idCategorias(Collections.singleton(1L))
                .autores(Collections.singleton(new ObraAutorRequest(1L, 1L)))
                .build();
        verifyRequest(obra);

        verifyRequest(new PaisRequest("P"));
        verifyRequest(new PlantaRequest("P", 1L));
        verifyRequest(new SalonRequest("S", 1L));
        verifyRequest(new TipoAutoriaRequest("T"));
    }

    private void verifyRequest(Object request) {
        assertThat(request.toString()).isNotBlank();
        assertThat(request.hashCode()).isNotZero();
        assertThat(request.equals(request)).isTrue();
        assertThat(request.equals(null)).isFalse();
    }
}
