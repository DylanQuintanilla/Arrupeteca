package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.persistence.entity.Planta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PlantaMapper Unit Tests")
class PlantaMapperTest {

    private final PlantaMapper mapper = Mappers.getMapper(PlantaMapper.class);

    @Test
    @DisplayName("Should map PlantaRequest to Planta entity")
    void shouldMapRequestToEntity() {
        // Given
        PlantaRequest request = PlantaRequest.builder()
                .nombre("Planta 1")
                .idEdificio(1L)
                .build();

        // When
        Planta entity = mapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getNombre()).isEqualTo(request.getNombre());
        assertThat(entity.isBorradoLogico()).isFalse();
    }

    @Test
    @DisplayName("Should update existing Planta entity from request")
    void shouldUpdateEntityFromRequest() {
        // Given
        PlantaRequest request = PlantaRequest.builder()
                .nombre("Planta Actualizada")
                .build();
        Planta entity = Planta.builder()
                .id(1L)
                .nombre("Planta 1")
                .build();

        // When
        mapper.updateEntity(request, entity);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNombre()).isEqualTo(request.getNombre());
    }

    @Test
    @DisplayName("Should return null when mapping null request")
    void shouldReturnNullWhenMappingNull() {
        // When
        Planta entity = mapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should not change anything when updating with null request")
    void shouldNotChangeAnythingWhenUpdatingWithNullRequest() {
        // Given
        Planta entity = Planta.builder()
                .id(1L)
                .nombre("Planta 1")
                .build();

        // When
        mapper.updateEntity(null, entity);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNombre()).isEqualTo("Planta 1");
    }
}
