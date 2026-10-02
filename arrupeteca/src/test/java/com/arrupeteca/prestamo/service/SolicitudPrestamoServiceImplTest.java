package com.arrupeteca.prestamo.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.prestamo.dto.request.SolicitudPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudPrestamoDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.SolicitudPrestamoResumen;
import com.arrupeteca.prestamo.internal.entity.EstadoSolicitud;
import com.arrupeteca.prestamo.internal.entity.SolicitudPrestamo;
import com.arrupeteca.prestamo.internal.mapper.SolicitudPrestamoMapper;
import com.arrupeteca.prestamo.internal.repository.EstadoSolicitudRepository;
import com.arrupeteca.prestamo.internal.repository.SolicitudPrestamoRepository;
import com.arrupeteca.prestamo.internal.service.impl.SolicitudPrestamoServiceImpl;
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
class SolicitudPrestamoServiceImplTest {

    @Mock
    private SolicitudPrestamoRepository solicitudPrestamoRepository;

    @Mock
    private EstadoSolicitudRepository estadoSolicitudRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SolicitudPrestamoMapper solicitudPrestamoMapper;

    @Mock
    private org.springframework.context.ApplicationEventPublisher events;

    @InjectMocks
    private SolicitudPrestamoServiceImpl solicitudPrestamoService;

    @Test
    @DisplayName("Should return page of solicitudes for admin")
    void shouldReturnPageForAdmin() {
        SolicitudPrestamoResumen resumen = SolicitudPrestamoResumen.builder()
                .id(1L)
                .idUsuario(10L)
                .nombreUsuario("Juan Pérez")
                .idEstadoSolicitud(1L)
                .estadoSolicitud("PENDIENTE")
                .fechaSolicitud(LocalDateTime.now())
                .creadoPorAdmin(false)
                .borradoLogico(false)
                .build();
        Page<SolicitudPrestamoResumen> page = new PageImpl<>(List.of(resumen));

        when(solicitudPrestamoRepository.busqueda(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        Page<SolicitudPrestamoResumen> result = solicitudPrestamoService.obtenerTodos(false, 10L, 1L, null, null, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("Should find by ID when exists")
    void shouldFindByIdWhenExists() {
        SolicitudPrestamo entity = SolicitudPrestamo.builder().id(1L).build();
        SolicitudPrestamoDetalleResponse detail = SolicitudPrestamoDetalleResponse.builder().id(1L).build();

        when(solicitudPrestamoRepository.busquedaPorId(1L, null)).thenReturn(Optional.of(entity));
        when(solicitudPrestamoMapper.toDetalleResponse(entity)).thenReturn(detail);

        SolicitudPrestamoDetalleResponse result = solicitudPrestamoService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("Should throw exception when ID not found")
    void shouldThrowWhenIdNotFound() {
        when(solicitudPrestamoRepository.busquedaPorId(99L, null)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> solicitudPrestamoService.obtenerPorId(99L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró una solicitud de préstamo con ID: 99");
    }

    @Test
    @DisplayName("Should create solicitud when valid")
    void shouldCreateWhenValid() {
        SolicitudPrestamoRequest request = SolicitudPrestamoRequest.builder()
                .idUsuario(JsonNullable.of(10L))
                .idEstadoSolicitud(JsonNullable.of(1L))
                .build();

        Usuario usuario = Usuario.builder().id(10L).borradoLogico(false).bloqueadoPorMora(false).build();
        EstadoSolicitud estado = EstadoSolicitud.builder().id(1L).nombre("PENDIENTE").borradoLogico(false).build();
        SolicitudPrestamo entity = SolicitudPrestamo.builder().id(1L).usuario(usuario).estadoSolicitud(estado).build();
        SolicitudPrestamoDetalleResponse detail = SolicitudPrestamoDetalleResponse.builder().id(1L).build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(estadoSolicitudRepository.findById(1L)).thenReturn(Optional.of(estado));
        when(solicitudPrestamoMapper.toEntity(request)).thenReturn(entity);
        when(solicitudPrestamoRepository.save(any(SolicitudPrestamo.class))).thenReturn(entity);
        when(solicitudPrestamoMapper.toDetalleResponse(entity)).thenReturn(detail);

        SolicitudPrestamoDetalleResponse result = solicitudPrestamoService.crear(request);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("Should throw exception on create when user blocked by mora")
    void shouldThrowWhenUserBlockedOnCreate() {
        SolicitudPrestamoRequest request = SolicitudPrestamoRequest.builder()
                .idUsuario(JsonNullable.of(10L))
                .build();

        Usuario usuario = Usuario.builder().id(10L).borradoLogico(false).bloqueadoPorMora(true).build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> solicitudPrestamoService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("bloqueado por mora");
    }

    @Test
    @DisplayName("Should activate and deactivate")
    void shouldChangeEstado() {
        when(solicitudPrestamoRepository.existsById(1L)).thenReturn(true);

        solicitudPrestamoService.cambiarEstado(1L, true);
        verify(solicitudPrestamoRepository).activar(1L);

        solicitudPrestamoService.cambiarEstado(1L, false);
        verify(solicitudPrestamoRepository).desactivar(1L);
    }
}
