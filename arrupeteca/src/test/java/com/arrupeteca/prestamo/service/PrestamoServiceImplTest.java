package com.arrupeteca.prestamo.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.prestamo.dto.request.PrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.PrestamoDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.PrestamoResumen;
import com.arrupeteca.prestamo.internal.entity.EstadoPrestamo;
import com.arrupeteca.prestamo.internal.entity.Prestamo;
import com.arrupeteca.prestamo.internal.mapper.PrestamoMapper;
import com.arrupeteca.prestamo.internal.repository.EstadoPrestamoRepository;
import com.arrupeteca.prestamo.internal.repository.PrestamoRepository;
import com.arrupeteca.prestamo.internal.repository.SolicitudPrestamoRepository;
import com.arrupeteca.prestamo.internal.service.impl.PrestamoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceImplTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private EstadoPrestamoRepository estadoPrestamoRepository;

    @Mock
    private SolicitudPrestamoRepository solicitudPrestamoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PrestamoMapper prestamoMapper;

    @Mock
    private org.springframework.context.ApplicationEventPublisher events;

    @InjectMocks
    private PrestamoServiceImpl prestamoService;

    @Test
    @DisplayName("Should return page of prestamos for admin")
    void shouldReturnPageForAdmin() {
        PrestamoResumen resumen = PrestamoResumen.builder()
                .id(1L)
                .idUsuario(10L)
                .nombreUsuario("Carlos Gómez")
                .idEstadoPrestamo(1L)
                .estadoPrestamo("ACTIVO")
                .fechaPrestamo(LocalDateTime.now())
                .borradoLogico(false)
                .build();
        Page<PrestamoResumen> page = new PageImpl<>(List.of(resumen));

        when(prestamoRepository.busqueda(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        Page<PrestamoResumen> result = prestamoService.obtenerTodos(false, 10L, 1L, null, null, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("Should find by ID when exists")
    void shouldFindByIdWhenExists() {
        Prestamo entity = Prestamo.builder().id(1L).build();
        PrestamoDetalleResponse detail = PrestamoDetalleResponse.builder().id(1L).build();

        when(prestamoRepository.busquedaPorId(1L, null)).thenReturn(Optional.of(entity));
        when(prestamoMapper.toDetalleResponse(entity)).thenReturn(detail);

        PrestamoDetalleResponse result = prestamoService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("Should throw exception when ID not found")
    void shouldThrowWhenIdNotFound() {
        when(prestamoRepository.busquedaPorId(99L, null)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prestamoService.obtenerPorId(99L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un préstamo con ID: 99");
    }

    @Test
    @DisplayName("Should create prestamo with manual custom fechaPrestamo")
    void shouldCreateWithCustomFechaPrestamo() {
        LocalDateTime customDate = LocalDateTime.of(2026, 3, 1, 10, 0);
        PrestamoRequest request = PrestamoRequest.builder()
                .idUsuario(JsonNullable.of(10L))
                .entregadoPor(JsonNullable.of(2L))
                .idEstadoPrestamo(JsonNullable.of(1L))
                .fechaPrestamo(JsonNullable.of(customDate))
                .build();

        Usuario usuario = Usuario.builder().id(10L).borradoLogico(false).bloqueadoPorMora(false).build();
        Usuario bibliotecario = Usuario.builder().id(2L).borradoLogico(false).build();
        EstadoPrestamo estado = EstadoPrestamo.builder().id(1L).nombre("ACTIVO").borradoLogico(false).build();
        Prestamo entity = Prestamo.builder().id(1L).fechaPrestamo(customDate).build();
        PrestamoDetalleResponse detail = PrestamoDetalleResponse.builder().id(1L).fechaPrestamo(customDate).build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(bibliotecario));
        when(estadoPrestamoRepository.findById(1L)).thenReturn(Optional.of(estado));
        when(prestamoMapper.toEntity(request)).thenReturn(entity);
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(entity);
        when(prestamoMapper.toDetalleResponse(entity)).thenReturn(detail);

        PrestamoDetalleResponse result = prestamoService.crear(request);

        assertThat(result).isEqualTo(detail);
        assertThat(result.fechaPrestamo()).isEqualTo(customDate);
    }

    @Test
    @DisplayName("Should activate and deactivate prestamo")
    void shouldChangeEstado() {
        when(prestamoRepository.existsById(1L)).thenReturn(true);

        prestamoService.cambiarEstado(1L, true);
        verify(prestamoRepository).activar(1L);

        prestamoService.cambiarEstado(1L, false);
        verify(prestamoRepository).desactivar(1L);
    }
}
