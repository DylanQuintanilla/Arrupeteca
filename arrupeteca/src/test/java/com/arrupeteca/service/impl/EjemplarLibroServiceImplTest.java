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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EjemplarLibroServiceImplTest {

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
    @DisplayName("obtenerTodosActivos - cover map branch")
    void shouldReturnActiveWithMap() {
        EjemplarLibro entity = new EjemplarLibro();
        when(repository.findByBorradoLogicoFalse(any())).thenReturn(new PageImpl<>(List.of(entity)));
        when(mapper.toResumen(entity)).thenReturn(mock(EjemplarLibroResumen.class));
        Page<EjemplarLibroResumen> result = service.obtenerTodosActivos(Pageable.unpaged());
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Success")
    void shouldReturnByIdActive() {
        EjemplarLibro entity = new EjemplarLibro();
        EjemplarLibroDetalleResponse detail = mock(EjemplarLibroDetalleResponse.class);
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDetalleResponse(entity)).thenReturn(detail);
        assertThat(service.obtenerPorIdActivo(1L)).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - NotFound")
    void shouldThrowWhenActiveNotFound() {
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorIdActivo(1L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("busquedaGlobal - Success")
    void shouldPerformGlobalSearch() {
        EjemplarLibro entity = new EjemplarLibro();
        when(repository.busquedaGlobal(any(), any())).thenReturn(new PageImpl<>(List.of(entity)));
        when(mapper.toResumen(entity)).thenReturn(mock(EjemplarLibroResumen.class));
        assertThat(service.busquedaGlobal("test", Pageable.unpaged())).isNotEmpty();
    }

    @Test
    @DisplayName("obtenerTodos - Success")
    void shouldReturnAllAdmin() {
        EjemplarLibro entity = new EjemplarLibro();
        when(repository.findAllProjectedBy(any())).thenReturn(new PageImpl<>(List.of(entity)));
        when(mapper.toResumen(entity)).thenReturn(mock(EjemplarLibroResumen.class));
        assertThat(service.obtenerTodos(Pageable.unpaged())).isNotEmpty();
    }

    @Test
    @DisplayName("obtenerPorId - Success")
    void shouldReturnById() {
        EjemplarLibro entity = new EjemplarLibro();
        EjemplarLibroDetalleResponse detail = mock(EjemplarLibroDetalleResponse.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDetalleResponse(entity)).thenReturn(detail);
        assertThat(service.obtenerPorId(1L)).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorId - NotFound")
    void shouldThrowWhenNotFound() {
        when(repository.findProjectedById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorId(1L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("busquedaMaestraAdmin - Full Branch Coverage")
    void shouldCoverBusquedaMaestraAdmin() {
        when(repository.busquedaMaestraAdmin(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        // All nulls
        service.busquedaMaestraAdmin(null, null, null, null, null, null, Pageable.unpaged());
        // String non-empty
        service.busquedaMaestraAdmin(false, 1L, 1L, "NUEVO", "DISPONIBLE", "term", Pageable.unpaged());
        // String empty
        service.busquedaMaestraAdmin(false, 1L, 1L, "", "", "term", Pageable.unpaged());
        // Enums
        service.busquedaMaestraAdmin(false, 1L, 1L, EstadoFisico.NUEVO, Disponibilidad.DISPONIBLE, "term", Pageable.unpaged());
        // Invalid String for valueOf
        assertThatThrownBy(() -> service.busquedaMaestraAdmin(false, 1L, 1L, "INVALID", "DISPONIBLE", "term", Pageable.unpaged()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.busquedaMaestraAdmin(false, 1L, 1L, "NUEVO", "INVALID", "term", Pageable.unpaged()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("crear - All relations branches")
    void shouldCreateWithAllCombinations() {
        Libro libro = new Libro();
        Mueble mueble = new Mueble();
        EjemplarLibro entity = new EjemplarLibro();
        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toDetalleResponse(any())).thenReturn(mock(EjemplarLibroDetalleResponse.class));

        // Both null
        service.crear(new EjemplarLibroRequest());
        // Only libro
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
        service.crear(EjemplarLibroRequest.builder().idLibro(1L).build());
        // Only mueble
        when(muebleRepository.findById(2L)).thenReturn(Optional.of(mueble));
        service.crear(EjemplarLibroRequest.builder().idMueble(2L).build());
    }

    @Test
    @DisplayName("actualizar - Success and NotFound")
    void shouldUpdate() {
        EjemplarLibro existing = new EjemplarLibro();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenReturn(existing);
        when(mapper.toDetalleResponse(any())).thenReturn(mock(EjemplarLibroDetalleResponse.class));

        service.actualizar(1L, new EjemplarLibroRequest());
        
        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(2L, new EjemplarLibroRequest())).isInstanceOf(ValidacionNegocioException.class);
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
        when(repository.findById(1L)).thenReturn(Optional.of(new EjemplarLibro()));
        when(libroRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(1L, EjemplarLibroRequest.builder().idLibro(2L).build()))
                .hasMessageContaining("El Libro indicado no existe");

        when(muebleRepository.findById(3L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(1L, EjemplarLibroRequest.builder().idMueble(3L).build()))
                .hasMessageContaining("El Mueble indicado no existe");
    }
}
