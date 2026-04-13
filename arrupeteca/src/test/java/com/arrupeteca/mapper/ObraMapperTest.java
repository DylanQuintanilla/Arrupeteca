package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.persistence.entity.Obra;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ObraMapper Unit Tests")
class ObraMapperTest {

    private final ObraMapper mapper = Mappers.getMapper(ObraMapper.class);

    @Test
    @DisplayName("Should map ObraRequest to Obra entity")
    void shouldMapRequestToEntity() {
        // Given
        ObraRequest request = ObraRequest.builder()
                .titulo("Obra A")
                .anioCreacion(2020)
                .resumen("Resumen de la obra A")
                .idGeneros(Set.of(1L))
                .idCategorias(Set.of(2L))
                .build();

        // When
        Obra entity = mapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getTitulo()).isEqualTo(request.getTitulo());
        assertThat(entity.getAnioCreacion()).isEqualTo(request.getAnioCreacion());
        assertThat(entity.getResumen()).isEqualTo(request.getResumen());
        assertThat(entity.isBorradoLogico()).isFalse();
    }

    @Test
    @DisplayName("Should update existing Obra entity from request")
    void shouldUpdateEntityFromRequest() {
        // Given
        ObraRequest request = ObraRequest.builder()
                .titulo("Obra Actualizada")
                .resumen("Resumen actualizado")
                .build();
        Obra entity = Obra.builder()
                .id(1L)
                .titulo("Obra Original")
                .resumen("Resumen original")
                .build();

        // When
        mapper.updateEntity(request, entity);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getTitulo()).isEqualTo(request.getTitulo());
        assertThat(entity.getResumen()).isEqualTo(request.getResumen());
    }

    @Test
    @DisplayName("Should return null when mapping null request")
    void shouldReturnNullWhenMappingNull() {
        // When
        Obra entity = mapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should not change anything when updating with null request")
    void shouldNotChangeAnythingWhenUpdatingWithNullRequest() {
        // Given
        Obra entity = Obra.builder()
                .id(1L)
                .titulo("Obra Original")
                .resumen("Resumen original")
                .build();

        // When
        mapper.updateEntity(null, entity);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getTitulo()).isEqualTo("Obra Original");
        assertThat(entity.getResumen()).isEqualTo("Resumen original");
    }
}
