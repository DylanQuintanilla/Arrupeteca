package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LibroMapper Unit Tests")
class LibroMapperTest {

    private final LibroMapper mapper = Mappers.getMapper(LibroMapper.class);

    @Test
    @DisplayName("Should map LibroRequest to Libro entity")
    void shouldMapRequestToEntity() {
        // Given
        LibroRequest request = LibroRequest.builder()
                .isbn("978-3-16-148410-0")
                .anioPublicacion(2021)
                .precio(new BigDecimal("29.99"))
                .urlImagen("http://image.url")
                .mora(new BigDecimal("0.50"))
                .grado(Grado.PRIMER_TO_NOVENO)
                .ciclo(Ciclo.PRIMER)
                .idObra(1L)
                .idFormato(2L)
                .idEditorial(3L)
                .idIdiomas(Set.of(4L, 5L))
                .build();

        // When
        Libro entity = mapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getIsbn()).isEqualTo(request.getIsbn());
        assertThat(entity.getAnioPublicacion()).isEqualTo(request.getAnioPublicacion());
        assertThat(entity.getPrecio()).isEqualTo(request.getPrecio());
        assertThat(entity.getUrlImagen()).isEqualTo(request.getUrlImagen());
        assertThat(entity.getMora()).isEqualTo(request.getMora());
        assertThat(entity.getGrado()).isEqualTo(request.getGrado());
        assertThat(entity.getCiclo()).isEqualTo(request.getCiclo());
        // IDs are likely ignored by the mapper as it doesn't have @Mapping for them
    }

    @Test
    @DisplayName("Should update existing Libro entity from request")
    void shouldUpdateEntityFromRequest() {
        // Given
        LibroRequest request = LibroRequest.builder()
                .isbn("978-3-16-148410-1")
                .anioPublicacion(2022)
                .build();
        Libro entity = Libro.builder()
                .id(1L)
                .isbn("978-3-16-148410-0")
                .anioPublicacion(2021)
                .build();

        // When
        mapper.updateEntity(request, entity);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getIsbn()).isEqualTo(request.getIsbn());
        assertThat(entity.getAnioPublicacion()).isEqualTo(request.getAnioPublicacion());
    }

    @Test
    @DisplayName("Should return null when mapping null request")
    void shouldReturnNullWhenMappingNull() {
        // When
        Libro entity = mapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should not change anything when updating with null request")
    void shouldNotChangeAnythingWhenUpdatingWithNullRequest() {
        // Given
        Libro entity = Libro.builder()
                .id(1L)
                .isbn("978-3-16-148410-0")
                .anioPublicacion(2021)
                .build();

        // When
        mapper.updateEntity(null, entity);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getIsbn()).isEqualTo("978-3-16-148410-0");
        assertThat(entity.getAnioPublicacion()).isEqualTo(2021);
    }
}
