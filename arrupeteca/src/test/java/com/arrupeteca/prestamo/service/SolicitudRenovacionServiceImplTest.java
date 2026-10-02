package com.arrupeteca.prestamo.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.prestamo.dto.request.SolicitudRenovacionRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudRenovacionDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.SolicitudRenovacionResumen;
import com.arrupeteca.prestamo.internal.entity.EjemplarPrestamo;
import com.arrupeteca.prestamo.internal.entity.EstadoSolicitud;
import com.arrupeteca.prestamo.internal.entity.SolicitudRenovacion;
import com.arrupeteca.prestamo.internal.mapper.SolicitudRenovacionMapper;
import com.arrupeteca.prestamo.internal.repository.EjemplarPrestamoRepository;
import com.arrupeteca.prestamo.internal.repository.EstadoSolicitudRepository;
import com.arrupeteca.prestamo.internal.repository.SolicitudRenovacionRepository;
import com.arrupeteca.prestamo.internal.service.impl.SolicitudRenovacionServiceImpl;
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
class SolicitudRenovacionServiceImplTest {

    @Mock
    private SolicitudRenovacionRepository renovacionRepository;

    @Mock
    private EjemplarPrestamoRepository ejemplarPrestamoRepository;

    @Mock
    private EstadoSolicitudRepository estadoSolicitudRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SolicitudRenovacionMapper renovacionMapper;

    @Mock
    private org.springframework.context.ApplicationEventPublisher events;

    @InjectMocks
    private SolicitudRenovacionServiceImpl renovacionService;

    @Test
    @DisplayName("Should return page of renovaciones for admin")
    void shouldReturnPageForAdmin() {
        SolicitudRenovacionResumen resumen = SolicitudRenovacionResumen.builder()
                .id(1L)
                .idEjemplarPrestamo(10L)
                .idUsuario(5L)
                .nombreUsuario("María López")
                .idEstadoSolicitud(1L)
                .estadoSolicitud("PENDIENTE")
                .diasSolicitados(7)
                .fechaSolicitud(LocalDateTime.now())
                .borradoLogico(false)
                .build();
        Page<SolicitudRenovacionResumen> page = new PageImpl<>(List.of(resumen));

        when(renovacionRepository.busqueda(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        Page<SolicitudRenovacionResumen> result = renovacionService.obtenerTodos(false, 5L, 1L, null, null, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("Should request renovacion when valid")
    void shouldRequestRenovacionWhenValid() {
        SolicitudRenovacionRequest request = SolicitudRenovacionRequest.builder()
                .idEjemplarPrestamo(JsonNullable.of(10L))
                .idUsuario(JsonNullable.of(5L))
                .diasSolicitados(JsonNullable.of(7))
                .build();

        EjemplarPrestamo ejemplar = EjemplarPrestamo.builder().id(10L).borradoLogico(false).build();
        Usuario usuario = Usuario.builder().id(5L).borradoLogico(false).bloqueadoPorMora(false).build();
        EstadoSolicitud estado = EstadoSolicitud.builder().id(1L).nombre("PENDIENTE").build();
        SolicitudRenovacion entity = SolicitudRenovacion.builder().id(1L).build();
        SolicitudRenovacionDetalleResponse response = SolicitudRenovacionDetalleResponse.builder().id(1L).build();

        when(ejemplarPrestamoRepository.findById(10L)).thenReturn(Optional.of(ejemplar));
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));
        when(estadoSolicitudRepository.findById(1L)).thenReturn(Optional.of(estado));
        when(renovacionMapper.toEntity(request)).thenReturn(entity);
        when(renovacionRepository.save(any(SolicitudRenovacion.class))).thenReturn(entity);
        when(renovacionMapper.toDetalleResponse(entity)).thenReturn(response);

        SolicitudRenovacionDetalleResponse result = renovacionService.solicitarRenovacion(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw when user is blocked by mora on request renovacion")
    void shouldThrowWhenUserBlocked() {
        SolicitudRenovacionRequest request = SolicitudRenovacionRequest.builder()
                .idEjemplarPrestamo(JsonNullable.of(10L))
                .idUsuario(JsonNullable.of(5L))
                .build();

        EjemplarPrestamo ejemplar = EjemplarPrestamo.builder().id(10L).borradoLogico(false).build();
        Usuario usuario = Usuario.builder().id(5L).borradoLogico(false).bloqueadoPorMora(true).build();

        when(ejemplarPrestamoRepository.findById(10L)).thenReturn(Optional.of(ejemplar));
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> renovacionService.solicitarRenovacion(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("bloqueado por mora");
    }

    @Test
    @DisplayName("Should activate and deactivate renovacion")
    void shouldChangeEstado() {
        when(renovacionRepository.existsById(1L)).thenReturn(true);

        renovacionService.cambiarEstado(1L, true);
        verify(renovacionRepository).activar(1L);

        renovacionService.cambiarEstado(1L, false);
        verify(renovacionRepository).desactivar(1L);
    }
}
