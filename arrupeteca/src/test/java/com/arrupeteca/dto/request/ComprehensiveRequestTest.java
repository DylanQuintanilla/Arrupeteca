package com.arrupeteca.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.Year;
import java.util.Collections;

class ComprehensiveRequestTest {

    @Test
    void shouldCoverAutorRequest() {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("Nombre"))
                .seudonimo(JsonNullable.of("Seudonimo"))
                .anioNacimiento(JsonNullable.of(Year.of(1900)))
                .anioFallecimiento(JsonNullable.of(Year.of(1980)))
                .biografia(JsonNullable.of("Bio"))
                .idPaisNacimiento(JsonNullable.of(1L))
                .idNacionalidad(JsonNullable.of(1L))
                .idGeneroPrincipal(JsonNullable.of(1L))
                .idObraCumbre(JsonNullable.of(1L))
                .build();

        assertThat(request.isFechasValidas()).isTrue();

        AutorRequest requestFechasInvalidas = AutorRequest.builder()
                .nombre(JsonNullable.of("Nombre"))
                .biografia(JsonNullable.of("Bio"))
                .anioNacimiento(JsonNullable.of(Year.of(2000)))
                .anioFallecimiento(JsonNullable.of(Year.of(1900)))
                .build();
        assertThat(requestFechasInvalidas.isFechasValidas()).isFalse();

        // Trigger Lombok/Record methods
        assertThat(request.toString()).contains("AutorRequest");
        AutorRequest other = AutorRequest.builder().build();
        assertThat(request.equals(other)).isFalse();
        assertThat(request.hashCode()).isNotZero();
    }

    @Test
    void shouldCoverAllOtherRequests() {
        // Just instantiate and trigger Lombok code for other requests
        verifyRequest(new CategoriaRequest("Cat"));
        verifyRequest(new EdificioRequest("Edif"));
        verifyRequest(new CicloRequest("Ciclo"));
        verifyRequest(new GradoRequest("Grado"));
        verifyRequest(new EstadoFisicoRequest("Estado"));
        verifyRequest(new DisponibilidadRequest("Disp"));
        
        EditorialRequest edit = EditorialRequest.builder()
                .nombre("N")
                .direccion("D")
                .correo("e@e.com")
                .telefono("1234-5678")
                .personaContacto("P")
                .build();
        verifyRequest(edit);

        verifyRequest(EjemplarLibroRequest.builder()
                .comentario(org.openapitools.jackson.nullable.JsonNullable.of("C"))
                .idEstadoFisico(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idDisponibilidad(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idLibro(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idMueble(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .build());
        verifyRequest(new FormatoRequest("F"));
        verifyRequest(new GeneroRequest("G"));
        verifyRequest(new IdiomaRequest("I"));
        
        LibroRequest libro = LibroRequest.builder()
                .isbn(org.openapitools.jackson.nullable.JsonNullable.of("123"))
                .anioPublicacion(org.openapitools.jackson.nullable.JsonNullable.of(2021))
                .idObra(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idFormato(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idEditorial(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idIdiomas(org.openapitools.jackson.nullable.JsonNullable.of(Collections.singleton(1L)))
                .idGrado(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idCiclo(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .build();
        verifyRequest(libro);

        verifyRequest(new MuebleRequest("M", 1L));
        verifyRequest(new NacionalidadRequest("N"));
        verifyRequest(new ObraAutorRequest(1L, 1L));
        
        ObraRequest obra = ObraRequest.builder()
                .titulo(org.openapitools.jackson.nullable.JsonNullable.of("T"))
                .anioCreacion(org.openapitools.jackson.nullable.JsonNullable.of(2020))
                .resumen(org.openapitools.jackson.nullable.JsonNullable.of("R"))
                .idGeneros(org.openapitools.jackson.nullable.JsonNullable.of(Collections.singleton(1L)))
                .idCategorias(org.openapitools.jackson.nullable.JsonNullable.of(Collections.singleton(1L)))
                .autores(org.openapitools.jackson.nullable.JsonNullable.of(Collections.singleton(new ObraAutorRequest(1L, 1L))))
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
