package com.arrupeteca.prestamo.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.prestamo.dto.request.EstadoPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.EstadoPrestamoResponse;
import com.arrupeteca.prestamo.persistence.entity.EstadoPrestamo;
import com.arrupeteca.prestamo.mapper.EstadoPrestamoMapper;
import com.arrupeteca.prestamo.persistence.repository.EstadoPrestamoRepository;
import com.arrupeteca.prestamo.service.impl.EstadoPrestamoServiceImpl;
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
class EstadoPrestamoServiceImplTest {

    @Mock
    private EstadoPrestamoRepository estadoPrestamoRepository;

    @Mock
    private EstadoPrestamoMapper estadoPrestamoMapper;

    @InjectMocks
    private EstadoPrestamoServiceImpl estadoPrestamoService;

    @Test
    @DisplayName("Should return all active estados de prestamo")
    void shouldReturnAllActive() {
        EstadoPrestamo entity = EstadoPrestamo.builder().id(1L).nombre("ACTIVO").build();
        EstadoPrestamoResponse response = new EstadoPrestamoResponse(1L, "ACTIVO", false);

        when(estadoPrestamoRepository.buscarActivosPorNombre(any())).thenReturn(List.of(entity));
        when(estadoPrestamoMapper.toResponse(entity)).thenReturn(response);

        List<EstadoPrestamoResponse> result = estadoPrestamoService.obtenerTodosActivos(null);

        assertThat(result).containsExactly(response);
        verify(estadoPrestamoRepository).buscarActivosPorNombre(null);
    }

    @Test
    @DisplayName("Should return page for admin")
    void shouldReturnAllAdmin() {
        EstadoPrestamo entity = EstadoPrestamo.builder().id(1L).nombre("ACTIVO").build();
        EstadoPrestamoResponse response = new EstadoPrestamoResponse(1L, "ACTIVO", false);
        Page<EstadoPrestamo> page = new PageImpl<>(List.of(entity));

        when(estadoPrestamoRepository.busquedaAdmin(any(), any(), any(Pageable.class))).thenReturn(page);
        when(estadoPrestamoMapper.toResponse(entity)).thenReturn(response);

        Page<EstadoPrestamoResponse> result = estadoPrestamoService.obtenerTodos("ACT", false, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should find by ID when exists")
    void shouldFindById() {
        EstadoPrestamo entity = EstadoPrestamo.builder().id(1L).nombre("ACTIVO").build();
        EstadoPrestamoResponse response = new EstadoPrestamoResponse(1L, "ACTIVO", false);

        when(estadoPrestamoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(estadoPrestamoMapper.toResponse(entity)).thenReturn(response);

        EstadoPrestamoResponse result = estadoPrestamoService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when ID not found")
    void shouldThrowWhenIdNotFound() {
        when(estadoPrestamoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estadoPrestamoService.obtenerPorId(99L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should create when valid")
    void shouldCreateWhenValid() {
        EstadoPrestamoRequest request = new EstadoPrestamoRequest("ACTIVO");
        EstadoPrestamo entity = EstadoPrestamo.builder().id(1L).nombre("ACTIVO").build();
        EstadoPrestamoResponse response = new EstadoPrestamoResponse(1L, "ACTIVO", false);

        when(estadoPrestamoRepository.existsByNombreIgnoreCase("ACTIVO")).thenReturn(false);
        when(estadoPrestamoMapper.toEntity(request)).thenReturn(entity);
        when(estadoPrestamoRepository.save(entity)).thenReturn(entity);
        when(estadoPrestamoMapper.toResponse(entity)).thenReturn(response);

        EstadoPrestamoResponse result = estadoPrestamoService.crear(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate")
    void shouldThrowWhenCreatingDuplicate() {
        EstadoPrestamoRequest request = new EstadoPrestamoRequest("ACTIVO");
        when(estadoPrestamoRepository.existsByNombreIgnoreCase("ACTIVO")).thenReturn(true);

        assertThatThrownBy(() -> estadoPrestamoService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe");
    }

    @Test
    @DisplayName("Should update when valid")
    void shouldUpdateWhenValid() {
        EstadoPrestamoRequest request = new EstadoPrestamoRequest("FINALIZADO");
        EstadoPrestamo existing = EstadoPrestamo.builder().id(1L).nombre("ACTIVO").build();
        EstadoPrestamoResponse response = new EstadoPrestamoResponse(1L, "FINALIZADO", false);

        when(estadoPrestamoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(estadoPrestamoRepository.existsByNombreIgnoreCase("FINALIZADO")).thenReturn(false);
        when(estadoPrestamoRepository.save(existing)).thenReturn(existing);
        when(estadoPrestamoMapper.toResponse(existing)).thenReturn(response);

        EstadoPrestamoResponse result = estadoPrestamoService.actualizar(1L, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should activate and deactivate")
    void shouldChangeEstado() {
        when(estadoPrestamoRepository.existsById(1L)).thenReturn(true);

        estadoPrestamoService.cambiarEstado(1L, true);
        verify(estadoPrestamoRepository).activar(1L);

        estadoPrestamoService.cambiarEstado(1L, false);
        verify(estadoPrestamoRepository).desactivar(1L);
    }
}

