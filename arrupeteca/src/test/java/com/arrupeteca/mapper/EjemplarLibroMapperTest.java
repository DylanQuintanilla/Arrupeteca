package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EjemplarLibroMapper Unit Tests")
class EjemplarLibroMapperTest {

    private final EjemplarLibroMapper mapper = Mappers.getMapper(EjemplarLibroMapper.class);

    @Test
    @DisplayName("Should map EjemplarLibroRequest to EjemplarLibro entity")
    void shouldMapRequestToEntity() {
        // Given
        EjemplarLibroRequest request = EjemplarLibroRequest.builder()
                .comentario("Comentario de prueba")
                .estadoFisico(EstadoFisico.BUEN_ESTADO)
                .disponibilidad(Disponibilidad.DISPONIBLE)
                .idLibro(1L)
                .idMueble(2L)
                .build();

        // When
        EjemplarLibro entity = mapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getComentario()).isEqualTo(request.getComentario());
        assertThat(entity.getEstadoFisico()).isEqualTo(request.getEstadoFisico());
        assertThat(entity.getDisponibilidad()).isEqualTo(request.getDisponibilidad());
        assertThat(entity.isBorradoLogico()).isFalse();
    }

    @Test
    @DisplayName("Should update existing EjemplarLibro entity from request")
    void shouldUpdateEntityFromRequest() {
        // Given
        EjemplarLibroRequest request = EjemplarLibroRequest.builder()
                .comentario("Comentario actualizado")
                .estadoFisico(EstadoFisico.DETERIORADO)
                .build();
        EjemplarLibro entity = EjemplarLibro.builder()
                .id(1L)
                .comentario("Comentario original")
                .estadoFisico(EstadoFisico.BUEN_ESTADO)
                .build();

        // When
        mapper.updateEntity(request, entity);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getComentario()).isEqualTo(request.getComentario());
        assertThat(entity.getEstadoFisico()).isEqualTo(request.getEstadoFisico());
    }

    @Test
    @DisplayName("Should return null when mapping null request")
    void shouldReturnNullWhenMappingNull() {
        // When
        EjemplarLibro entity = mapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should not change anything when updating with null request")
    void shouldNotChangeAnythingWhenUpdatingWithNullRequest() {
        // Given
        EjemplarLibro entity = EjemplarLibro.builder()
                .id(1L)
                .comentario("Comentario original")
                .estadoFisico(EstadoFisico.BUEN_ESTADO)
                .build();

        // When
        mapper.updateEntity(null, entity);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getComentario()).isEqualTo("Comentario original");
        assertThat(entity.getEstadoFisico()).isEqualTo(EstadoFisico.BUEN_ESTADO);
    }
}
