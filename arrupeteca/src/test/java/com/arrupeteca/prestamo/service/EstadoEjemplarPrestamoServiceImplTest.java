package com.arrupeteca.prestamo.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.prestamo.dto.request.EstadoEjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.EstadoEjemplarPrestamoResponse;
import com.arrupeteca.prestamo.persistence.entity.EstadoEjemplarPrestamo;
import com.arrupeteca.prestamo.mapper.EstadoEjemplarPrestamoMapper;
import com.arrupeteca.prestamo.persistence.repository.EstadoEjemplarPrestamoRepository;
import com.arrupeteca.prestamo.service.impl.EstadoEjemplarPrestamoServiceImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoEjemplarPrestamoServiceImplTest {

    @Mock
    private EstadoEjemplarPrestamoRepository estadoEjemplarPrestamoRepository;

    @Mock
    private EstadoEjemplarPrestamoMapper estadoEjemplarPrestamoMapper;

    @InjectMocks
    private EstadoEjemplarPrestamoServiceImpl estadoEjemplarPrestamoService;

    @Test
    @DisplayName("Should return all active estados de ejemplar de prestamo")
    void shouldReturnAllActive() {
        EstadoEjemplarPrestamo entity = EstadoEjemplarPrestamo.builder().id(1L).nombre("EN_PRESTAMO").build();
        EstadoEjemplarPrestamoResponse response = new EstadoEjemplarPrestamoResponse(1L, "EN_PRESTAMO", false);

        when(estadoEjemplarPrestamoRepository.buscarActivosPorNombre(any())).thenReturn(List.of(entity));
        when(estadoEjemplarPrestamoMapper.toResponse(entity)).thenReturn(response);

        List<EstadoEjemplarPrestamoResponse> result = estadoEjemplarPrestamoService.obtenerTodosActivos(null);

        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("Should return page for admin")
    void shouldReturnAllAdmin() {
        EstadoEjemplarPrestamo entity = EstadoEjemplarPrestamo.builder().id(1L).nombre("EN_PRESTAMO").build();
        EstadoEjemplarPrestamoResponse response = new EstadoEjemplarPrestamoResponse(1L, "EN_PRESTAMO", false);
        Page<EstadoEjemplarPrestamo> page = new PageImpl<>(List.of(entity));

        when(estadoEjemplarPrestamoRepository.busquedaAdmin(any(), any(), any(Pageable.class))).thenReturn(page);
        when(estadoEjemplarPrestamoMapper.toResponse(entity)).thenReturn(response);

        Page<EstadoEjemplarPrestamoResponse> result = estadoEjemplarPrestamoService.obtenerTodos("PRESTAMO", false, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should find by ID when exists")
    void shouldFindById() {
        EstadoEjemplarPrestamo entity = EstadoEjemplarPrestamo.builder().id(1L).nombre("EN_PRESTAMO").build();
        EstadoEjemplarPrestamoResponse response = new EstadoEjemplarPrestamoResponse(1L, "EN_PRESTAMO", false);

        when(estadoEjemplarPrestamoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(estadoEjemplarPrestamoMapper.toResponse(entity)).thenReturn(response);

        EstadoEjemplarPrestamoResponse result = estadoEjemplarPrestamoService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should create when valid")
    void shouldCreateWhenValid() {
        EstadoEjemplarPrestamoRequest request = new EstadoEjemplarPrestamoRequest("DEVUELTO_A_TIEMPO");
        EstadoEjemplarPrestamo entity = EstadoEjemplarPrestamo.builder().id(1L).nombre("DEVUELTO_A_TIEMPO").build();
        EstadoEjemplarPrestamoResponse response = new EstadoEjemplarPrestamoResponse(1L, "DEVUELTO_A_TIEMPO", false);

        when(estadoEjemplarPrestamoRepository.existsByNombreIgnoreCase("DEVUELTO_A_TIEMPO")).thenReturn(false);
        when(estadoEjemplarPrestamoMapper.toEntity(request)).thenReturn(entity);
        when(estadoEjemplarPrestamoRepository.save(entity)).thenReturn(entity);
        when(estadoEjemplarPrestamoMapper.toResponse(entity)).thenReturn(response);

        EstadoEjemplarPrestamoResponse result = estadoEjemplarPrestamoService.crear(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should activate and deactivate")
    void shouldChangeEstado() {
        when(estadoEjemplarPrestamoRepository.existsById(1L)).thenReturn(true);

        estadoEjemplarPrestamoService.cambiarEstado(1L, true);
        verify(estadoEjemplarPrestamoRepository).activar(1L);

        estadoEjemplarPrestamoService.cambiarEstado(1L, false);
        verify(estadoEjemplarPrestamoRepository).desactivar(1L);
    }
}

