package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.persistence.entity.Mueble;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MuebleMapper Unit Tests")
class MuebleMapperTest {

    private final MuebleMapper mapper = Mappers.getMapper(MuebleMapper.class);

    @Test
    @DisplayName("Should map MuebleRequest to Mueble entity")
    void shouldMapRequestToEntity() {
        // Given
        MuebleRequest request = MuebleRequest.builder()
                .nombre("Mueble A")
                .idSalon(1L)
                .build();

        // When
        Mueble entity = mapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getNombre()).isEqualTo(request.getNombre());
        assertThat(entity.isBorradoLogico()).isFalse();
    }

    @Test
    @DisplayName("Should update existing Mueble entity from request")
    void shouldUpdateEntityFromRequest() {
        // Given
        MuebleRequest request = MuebleRequest.builder()
                .nombre("Mueble Actualizado")
                .build();
        Mueble entity = Mueble.builder()
                .id(1L)
                .nombre("Mueble Original")
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
        Mueble entity = mapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should not change anything when updating with null request")
    void shouldNotChangeAnythingWhenUpdatingWithNullRequest() {
        // Given
        Mueble entity = Mueble.builder()
                .id(1L)
                .nombre("Mueble Original")
                .build();

        // When
        mapper.updateEntity(null, entity);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNombre()).isEqualTo("Mueble Original");
    }
}
