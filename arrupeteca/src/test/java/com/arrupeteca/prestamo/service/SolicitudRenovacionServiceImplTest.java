package com.arrupeteca.prestamo.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.prestamo.dto.request.SolicitudRenovacionRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudRenovacionDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.SolicitudRenovacionResumen;
import com.arrupeteca.prestamo.persistence.entity.EjemplarPrestamo;
import com.arrupeteca.prestamo.persistence.entity.EstadoSolicitud;
import com.arrupeteca.prestamo.persistence.entity.Prestamo;
import com.arrupeteca.prestamo.persistence.entity.SolicitudRenovacion;
import com.arrupeteca.prestamo.mapper.SolicitudRenovacionMapper;
import com.arrupeteca.prestamo.persistence.repository.EjemplarPrestamoRepository;
import com.arrupeteca.prestamo.persistence.repository.EstadoSolicitudRepository;
import com.arrupeteca.prestamo.persistence.repository.PrestamoRepository;
import com.arrupeteca.prestamo.persistence.repository.SolicitudRenovacionRepository;
import com.arrupeteca.prestamo.service.impl.SolicitudRenovacionServiceImpl;
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
    private PrestamoRepository prestamoRepository;

    @Mock
    private EstadoSolicitudRepository estadoSolicitudRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SolicitudRenovacionMapper renovacionMapper;

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
        when(renovacionRepository.saveAndFlush(any(SolicitudRenovacion.class))).thenReturn(entity);
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

    @Test
    @DisplayName("Should extend prestamo fechaLimiteDevolucion when aprobada")
    void shouldExtendPrestamoFechaLimiteWhenAprobada() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(2);
        LocalDateTime limiteOriginal = LocalDateTime.now().plusDays(3);
        Prestamo prestamo = Prestamo.builder()
                .id(5L)
                .fechaPrestamo(inicio)
                .fechaLimiteDevolucion(limiteOriginal)
                .build();
        EjemplarPrestamo ejemplarPrestamo = EjemplarPrestamo.builder().id(10L).prestamo(prestamo).build();
        EstadoSolicitud aprobada = EstadoSolicitud.builder().id(2L).nombre("APROBADA").build();
        SolicitudRenovacion solicitud = SolicitudRenovacion.builder()
                .id(1L)
                .ejemplarPrestamo(ejemplarPrestamo)
                .diasSolicitados(5)
                .build();

        SolicitudRenovacionRequest request = SolicitudRenovacionRequest.builder()
                .idEstadoSolicitud(JsonNullable.of(2L))
                .motivoRespuesta(JsonNullable.of("Aprobada"))
                .build();
        SolicitudRenovacionDetalleResponse response = SolicitudRenovacionDetalleResponse.builder().id(1L).idEstadoSolicitud(2L).build();

        when(renovacionRepository.busquedaPorId(1L, null)).thenReturn(Optional.of(solicitud));
        when(estadoSolicitudRepository.findById(2L)).thenReturn(Optional.of(aprobada));
        when(renovacionRepository.saveAndFlush(solicitud)).thenReturn(solicitud);
        when(renovacionMapper.toDetalleResponse(solicitud)).thenReturn(response);

        renovacionService.responderRenovacion(1L, request);

        assertThat(prestamo.getFechaLimiteDevolucion()).isEqualTo(limiteOriginal.plusDays(5));
        verify(prestamoRepository).save(prestamo);
    }
}
