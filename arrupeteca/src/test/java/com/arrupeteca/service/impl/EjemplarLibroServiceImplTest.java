package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.dto.response.detail.EjemplarLibroDetalleResponse;
import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EjemplarLibroMapper;
import com.arrupeteca.persistence.entity.Disponibilidad;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.EstadoFisico;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.repository.DisponibilidadRepository;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
import com.arrupeteca.persistence.repository.EstadoFisicoRepository;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.persistence.repository.MuebleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EjemplarLibroServiceImplTest extends BaseServiceTest {

    @Mock
    private EjemplarLibroRepository repository;

    @Mock
    private EjemplarLibroMapper mapper;

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private MuebleRepository muebleRepository;

    @Mock
    private EstadoFisicoRepository estadoFisicoRepository;

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @InjectMocks
    private EjemplarLibroServiceImpl service;

    @Test
    @DisplayName("busquedaAdmin - All branches")
    void busquedaAdmin() {
        EjemplarLibroResumen summary = mock(EjemplarLibroResumen.class);
        Page<EjemplarLibroResumen> page = new PageImpl<>(List.of(summary));
        
        when(repository.busqueda(eq(false), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        Page<EjemplarLibroResumen> result = service.busquedaAdmin(false, 1L, 1L, 1L, 1L, 1L, 1L, 1L, "term", Pageable.unpaged());
        assertThat(result.getContent()).containsExactly(summary);
    }

    @Test
    @DisplayName("busquedaAdminPorId - Success and NotFound")
    void busquedaAdminPorId() {
        EjemplarLibro entity = EjemplarLibro.builder().id(1L).build();
        EjemplarLibroDetalleResponse detail = mock(EjemplarLibroDetalleResponse.class);
        when(repository.busquedaPorId(1L, null)).thenReturn(Optional.of(entity));
        when(mapper.toDetalleResponse(any())).thenReturn(detail);

        assertThat(service.busquedaAdminPorId(1L)).isEqualTo(detail);

        when(repository.busquedaPorId(2L, null)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.busquedaAdminPorId(2L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("crear - Success path")
    void shouldCreateWithAllCombinations() {
        Libro libro = Libro.builder().build();
        Mueble mueble = Mueble.builder().build();
        EstadoFisico ef = EstadoFisico.builder().build();
        Disponibilidad disp = Disponibilidad.builder().build();
        EjemplarLibro entity = EjemplarLibro.builder().build();

        EjemplarLibroRequest request = EjemplarLibroRequest.builder()
                .idLibro(JsonNullable.of(1L))
                .idMueble(JsonNullable.of(2L))
                .idEstadoFisico(JsonNullable.of(1L))
                .idDisponibilidad(JsonNullable.of(1L))
                .build();

        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.saveAndFlush(any())).thenReturn(entity);
        when(mapper.toDetalleResponse(any())).thenReturn(mock(EjemplarLibroDetalleResponse.class));
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
        when(muebleRepository.findById(2L)).thenReturn(Optional.of(mueble));
        when(estadoFisicoRepository.findById(1L)).thenReturn(Optional.of(ef));
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(disp));

        service.crear(request);
        verify(repository).saveAndFlush(entity);
    }

    @Test
    @DisplayName("actualizar - Success and NotFound")
    void shouldUpdate() {
        EjemplarLibro existing = EjemplarLibro.builder().id(1L).version(0L).build();
        EjemplarLibroRequest request = EjemplarLibroRequest.builder()
                .idLibro(JsonNullable.of(1L))
                .idMueble(JsonNullable.of(2L))
                .idEstadoFisico(JsonNullable.of(1L))
                .idDisponibilidad(JsonNullable.of(1L))
                .version(0L)
                .build();

        when(repository.busquedaPorId(1L, null)).thenReturn(Optional.of(existing));
        when(repository.saveAndFlush(any())).thenReturn(existing);
        when(mapper.toDetalleResponse(any())).thenReturn(mock(EjemplarLibroDetalleResponse.class));
        when(libroRepository.findById(1L)).thenReturn(Optional.of(Libro.builder().build()));
        when(muebleRepository.findById(2L)).thenReturn(Optional.of(Mueble.builder().build()));
        when(estadoFisicoRepository.findById(1L)).thenReturn(Optional.of(EstadoFisico.builder().build()));
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(Disponibilidad.builder().build()));

        service.actualizar(1L, request);

        when(repository.busquedaPorId(2L, null)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(2L, request)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("cambiarEstado - All branches")
    void shouldChangeStatus() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, true);
        verify(repository).activar(1L);
        service.cambiarEstado(1L, false);
        verify(repository).desactivar(1L);

        when(repository.existsById(2L)).thenReturn(false);
        assertThatThrownBy(() -> service.cambiarEstado(2L, true)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("asignarRelaciones - Exception branches")
    void shouldThrowWhenRelationNotFound() {
        EjemplarLibro existing = EjemplarLibro.builder().id(1L).version(0L).build();
        when(repository.busquedaPorId(1L, null)).thenReturn(Optional.of(existing));
        when(libroRepository.findById(2L)).thenReturn(Optional.empty());

        EjemplarLibroRequest reqLibro = EjemplarLibroRequest.builder()
                .idLibro(JsonNullable.of(2L))
                .version(0L)
                .build();

        assertThatThrownBy(() -> service.actualizar(1L, reqLibro))
                .isInstanceOf(ValidacionNegocioException.class)
                .satisfies(ex -> {
                    ValidacionNegocioException vne = (ValidacionNegocioException) ex;
                    assertThat(vne.getErrores()).contains("El Libro con ID 2 no existe.");
                });

        when(libroRepository.findById(2L)).thenReturn(Optional.of(Libro.builder().build()));
        when(muebleRepository.findById(3L)).thenReturn(Optional.empty());

        EjemplarLibroRequest reqMueble = EjemplarLibroRequest.builder()
                .idLibro(JsonNullable.of(2L))
                .idMueble(JsonNullable.of(3L))
                .version(0L)
                .build();

        assertThatThrownBy(() -> service.actualizar(1L, reqMueble))
                .isInstanceOf(ValidacionNegocioException.class)
                .satisfies(ex -> {
                    ValidacionNegocioException vne = (ValidacionNegocioException) ex;
                    assertThat(vne.getErrores()).contains("El Mueble con ID 3 no existe.");
                });
    }
}

