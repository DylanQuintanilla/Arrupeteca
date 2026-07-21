package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.dto.response.detail.EjemplarLibroDetalleResponse;
import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EjemplarLibroMapper;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
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

    @InjectMocks
    private EjemplarLibroServiceImpl service;

    @Test
    @DisplayName("obtenerTodosActivos - Should return summary page")
    void shouldReturnActive() {
        EjemplarLibro entity = EjemplarLibro.builder().id(1L).build();
        Page<EjemplarLibro> page = new PageImpl<>(List.of(entity));
        EjemplarLibroResumen summary = mock(EjemplarLibroResumen.class);
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(mapper.toResumen(any())).thenReturn(summary);

        Page<EjemplarLibroResumen> result = service.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(summary);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Success and NotFound")
    void shouldReturnById() {
        EjemplarLibro entity = EjemplarLibro.builder().id(1L).build();
        EjemplarLibroDetalleResponse detail = mock(EjemplarLibroDetalleResponse.class);
        when(repository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDetalleResponse(any())).thenReturn(detail);

        assertThat(service.obtenerPorIdActivo(1L)).isEqualTo(detail);

        when(repository.findByIdAndBorradoLogicoFalse(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorIdActivo(2L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("busquedaGlobal - Should return summary page")
    void shouldSearchGlobally() {
        EjemplarLibro entity = EjemplarLibro.builder().id(1L).build();
        Page<EjemplarLibro> page = new PageImpl<>(List.of(entity));
        EjemplarLibroResumen summary = mock(EjemplarLibroResumen.class);
        when(repository.busquedaGlobal(eq("term"), any(Pageable.class))).thenReturn(page);
        when(mapper.toResumen(any())).thenReturn(summary);

        Page<EjemplarLibroResumen> result = service.busquedaGlobal("term", Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(summary);
    }

    @Test
    @DisplayName("obtenerTodos - Should return all page")
    void shouldReturnAll() {
        EjemplarLibro entity = EjemplarLibro.builder().id(1L).build();
        Page<EjemplarLibro> page = new PageImpl<>(List.of(entity));
        EjemplarLibroResumen summary = mock(EjemplarLibroResumen.class);
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toResumen(any())).thenReturn(summary);

        Page<EjemplarLibroResumen> result = service.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(summary);
    }

    @Test
    @DisplayName("obtenerPorId - Success and NotFound")
    void shouldReturnByIdAdmin() {
        EjemplarLibro entity = EjemplarLibro.builder().id(1L).build();
        EjemplarLibroDetalleResponse detail = mock(EjemplarLibroDetalleResponse.class);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDetalleResponse(any())).thenReturn(detail);

        assertThat(service.obtenerPorId(1L)).isEqualTo(detail);

        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorId(2L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("busquedaMaestraAdmin - All branches")
    void shouldPerformMasterSearch() {
        EjemplarLibro entity = EjemplarLibro.builder().id(1L).build();
        Page<EjemplarLibro> page = new PageImpl<>(List.of(entity));
        EjemplarLibroResumen summary = mock(EjemplarLibroResumen.class);
        
        when(repository.busquedaMaestraAdmin(eq(false), eq(1L), eq(1L), 
                eq(EstadoFisico.BUEN_ESTADO), eq(Disponibilidad.DISPONIBLE), eq("term"), any(Pageable.class)))
                .thenReturn(page);
        when(mapper.toResumen(any())).thenReturn(summary);

        // String param types
        Page<EjemplarLibroResumen> result = service.busquedaMaestraAdmin(false, 1L, 1L, "BUEN_ESTADO", "DISPONIBLE", "term", Pageable.unpaged());
        assertThat(result.getContent()).containsExactly(summary);

        // Enum param types
        service.busquedaMaestraAdmin(false, 1L, 1L, EstadoFisico.BUEN_ESTADO, Disponibilidad.DISPONIBLE, "term", Pageable.unpaged());

        // Invalid String for valueOf
        assertThatThrownBy(() -> service.busquedaMaestraAdmin(false, 1L, 1L, "INVALID", "DISPONIBLE", "term", Pageable.unpaged()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.busquedaMaestraAdmin(false, 1L, 1L, "BUEN_ESTADO", "INVALID", "term", Pageable.unpaged()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("crear - Success path")
    void shouldCreateWithAllCombinations() {
        Libro libro = Libro.builder().build();
        Mueble mueble = Mueble.builder().build();
        EjemplarLibro entity = EjemplarLibro.builder().build();

        EjemplarLibroRequest request = EjemplarLibroRequest.builder()
                .idLibro(JsonNullable.of(1L))
                .idMueble(JsonNullable.of(2L))
                .estadoFisico(JsonNullable.of(EstadoFisico.BUEN_ESTADO))
                .disponibilidad(JsonNullable.of(Disponibilidad.DISPONIBLE))
                .build();

        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toDetalleResponse(any())).thenReturn(mock(EjemplarLibroDetalleResponse.class));
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
        when(muebleRepository.findById(2L)).thenReturn(Optional.of(mueble));

        service.crear(request);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("actualizar - Success and NotFound")
    void shouldUpdate() {
        EjemplarLibro existing = EjemplarLibro.builder().id(1L).build();
        EjemplarLibroRequest request = EjemplarLibroRequest.builder()
                .idLibro(JsonNullable.of(1L))
                .idMueble(JsonNullable.of(2L))
                .estadoFisico(JsonNullable.of(EstadoFisico.BUEN_ESTADO))
                .disponibilidad(JsonNullable.of(Disponibilidad.DISPONIBLE))
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenReturn(existing);
        when(mapper.toDetalleResponse(any())).thenReturn(mock(EjemplarLibroDetalleResponse.class));
        when(libroRepository.findById(1L)).thenReturn(Optional.of(Libro.builder().build()));
        when(muebleRepository.findById(2L)).thenReturn(Optional.of(Mueble.builder().build()));

        service.actualizar(1L, request);

        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(2L, request)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("cambiarEstado - All branches")
    void shouldChangeStatus() {
        when(repository.countByIdIncludeDeleted(1L)).thenReturn(1L);
        service.cambiarEstado(1L, true);
        verify(repository).activar(1L);
        service.cambiarEstado(1L, false);
        verify(repository).desactivar(1L);

        when(repository.countByIdIncludeDeleted(2L)).thenReturn(0L);
        assertThatThrownBy(() -> service.cambiarEstado(2L, true)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("asignarRelaciones - Exception branches")
    void shouldThrowWhenRelationNotFound() {
        EjemplarLibro existing = EjemplarLibro.builder().id(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(libroRepository.findById(2L)).thenReturn(Optional.empty());

        EjemplarLibroRequest reqLibro = EjemplarLibroRequest.builder()
                .idLibro(JsonNullable.of(2L))
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
                .build();

        assertThatThrownBy(() -> service.actualizar(1L, reqMueble))
                .isInstanceOf(ValidacionNegocioException.class)
                .satisfies(ex -> {
                    ValidacionNegocioException vne = (ValidacionNegocioException) ex;
                    assertThat(vne.getErrores()).contains("El Mueble con ID 3 no existe.");
                });
    }
}
