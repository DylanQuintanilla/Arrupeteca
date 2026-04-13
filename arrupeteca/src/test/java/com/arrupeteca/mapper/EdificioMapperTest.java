package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.persistence.entity.Edificio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EdificioMapper Unit Tests")
class EdificioMapperTest {

    private final EdificioMapper mapper = Mappers.getMapper(EdificioMapper.class);

    @Test
    @DisplayName("Should map EdificioRequest to Edificio entity")
    void shouldMapRequestToEntity() {
        // Given
        EdificioRequest request = EdificioRequest.builder()
                .nombre("Edificio A")
                .build();

        // When
        Edificio entity = mapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getNombre()).isEqualTo(request.getNombre());
        assertThat(entity.isBorradoLogico()).isFalse();
    }

    @Test
    @DisplayName("Should update existing Edificio entity from request")
    void shouldUpdateEntityFromRequest() {
        // Given
        EdificioRequest request = EdificioRequest.builder()
                .nombre("Edificio Actualizado")
                .build();
        Edificio entity = Edificio.builder()
                .id(1L)
                .nombre("Edificio Original")
                .borradoLogico(false)
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
        Edificio entity = mapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should not change anything when updating with null request")
    void shouldNotChangeAnythingWhenUpdatingWithNullRequest() {
        // Given
        Edificio entity = Edificio.builder()
                .id(1L)
                .nombre("Edificio Original")
                .borradoLogico(false)
                .build();

        // When
        mapper.updateEntity(null, entity);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNombre()).isEqualTo("Edificio Original");
        assertThat(entity.isBorradoLogico()).isFalse();
    }
}
