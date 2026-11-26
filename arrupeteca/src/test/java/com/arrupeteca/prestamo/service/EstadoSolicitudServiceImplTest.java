package com.arrupeteca.prestamo.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.prestamo.dto.request.EstadoSolicitudRequest;
import com.arrupeteca.prestamo.dto.response.EstadoSolicitudResponse;
import com.arrupeteca.prestamo.persistence.entity.EstadoSolicitud;
import com.arrupeteca.prestamo.mapper.EstadoSolicitudMapper;
import com.arrupeteca.prestamo.persistence.repository.EstadoSolicitudRepository;
import com.arrupeteca.prestamo.service.impl.EstadoSolicitudServiceImpl;
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
class EstadoSolicitudServiceImplTest {

    @Mock
    private EstadoSolicitudRepository estadoSolicitudRepository;

    @Mock
    private EstadoSolicitudMapper estadoSolicitudMapper;

    @InjectMocks
    private EstadoSolicitudServiceImpl estadoSolicitudService;

    @Test
    @DisplayName("Should return all active estados de solicitud")
    void shouldReturnAllActive() {
        EstadoSolicitud entity = EstadoSolicitud.builder().id(1L).nombre("PENDIENTE").build();
        EstadoSolicitudResponse response = new EstadoSolicitudResponse(1L, "PENDIENTE", false);

        when(estadoSolicitudRepository.buscarActivosPorNombre(any())).thenReturn(List.of(entity));
        when(estadoSolicitudMapper.toResponse(entity)).thenReturn(response);

        List<EstadoSolicitudResponse> result = estadoSolicitudService.obtenerTodosActivos(null);

        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("Should return page for admin")
    void shouldReturnAllAdmin() {
        EstadoSolicitud entity = EstadoSolicitud.builder().id(1L).nombre("PENDIENTE").build();
        EstadoSolicitudResponse response = new EstadoSolicitudResponse(1L, "PENDIENTE", false);
        Page<EstadoSolicitud> page = new PageImpl<>(List.of(entity));

        when(estadoSolicitudRepository.busquedaAdmin(any(), any(), any(Pageable.class))).thenReturn(page);
        when(estadoSolicitudMapper.toResponse(entity)).thenReturn(response);

        Page<EstadoSolicitudResponse> result = estadoSolicitudService.obtenerTodos("PEND", false, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should find by ID when exists")
    void shouldFindById() {
        EstadoSolicitud entity = EstadoSolicitud.builder().id(1L).nombre("PENDIENTE").build();
        EstadoSolicitudResponse response = new EstadoSolicitudResponse(1L, "PENDIENTE", false);

        when(estadoSolicitudRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(estadoSolicitudMapper.toResponse(entity)).thenReturn(response);

        EstadoSolicitudResponse result = estadoSolicitudService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should create when valid")
    void shouldCreateWhenValid() {
        EstadoSolicitudRequest request = new EstadoSolicitudRequest("EN_PREPARACION");
        EstadoSolicitud entity = EstadoSolicitud.builder().id(1L).nombre("EN_PREPARACION").build();
        EstadoSolicitudResponse response = new EstadoSolicitudResponse(1L, "EN_PREPARACION", false);

        when(estadoSolicitudRepository.existsByNombreIgnoreCase("EN_PREPARACION")).thenReturn(false);
        when(estadoSolicitudMapper.toEntity(request)).thenReturn(entity);
        when(estadoSolicitudRepository.save(entity)).thenReturn(entity);
        when(estadoSolicitudMapper.toResponse(entity)).thenReturn(response);

        EstadoSolicitudResponse result = estadoSolicitudService.crear(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should activate and deactivate")
    void shouldChangeEstado() {
        when(estadoSolicitudRepository.existsById(1L)).thenReturn(true);

        estadoSolicitudService.cambiarEstado(1L, true);
        verify(estadoSolicitudRepository).activar(1L);

        estadoSolicitudService.cambiarEstado(1L, false);
        verify(estadoSolicitudRepository).desactivar(1L);
    }
}

