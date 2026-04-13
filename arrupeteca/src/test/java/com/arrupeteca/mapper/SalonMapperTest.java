package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.persistence.entity.Salon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SalonMapper Unit Tests")
class SalonMapperTest {

    private final SalonMapper mapper = Mappers.getMapper(SalonMapper.class);

    @Test
    @DisplayName("Should map SalonRequest to Salon entity")
    void shouldMapRequestToEntity() {
        // Given
        SalonRequest request = SalonRequest.builder()
                .nombre("Salon 1")
                .idPlanta(1L)
                .build();

        // When
        Salon entity = mapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getNombre()).isEqualTo(request.getNombre());
        assertThat(entity.isBorradoLogico()).isFalse();
    }

    @Test
    @DisplayName("Should update existing Salon entity from request")
    void shouldUpdateEntityFromRequest() {
        // Given
        SalonRequest request = SalonRequest.builder()
                .nombre("Salon Actualizado")
                .build();
        Salon entity = Salon.builder()
                .id(1L)
                .nombre("Salon 1")
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
        Salon entity = mapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should not change anything when updating with null request")
    void shouldNotChangeAnythingWhenUpdatingWithNullRequest() {
        // Given
        Salon entity = Salon.builder()
                .id(1L)
                .nombre("Salon 1")
                .build();

        // When
        mapper.updateEntity(null, entity);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNombre()).isEqualTo("Salon 1");
    }
}
