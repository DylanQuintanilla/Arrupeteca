package com.arrupeteca.prestamo.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.prestamo.dto.request.SolicitudPrestamoDetalleRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudPrestamoDetalleItemResponse;
import com.arrupeteca.prestamo.internal.entity.SolicitudPrestamo;
import com.arrupeteca.prestamo.internal.entity.SolicitudPrestamoDetalle;
import com.arrupeteca.prestamo.internal.mapper.SolicitudPrestamoDetalleMapper;
import com.arrupeteca.prestamo.internal.repository.SolicitudPrestamoDetalleRepository;
import com.arrupeteca.prestamo.internal.repository.SolicitudPrestamoRepository;
import com.arrupeteca.prestamo.internal.service.impl.SolicitudPrestamoDetalleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudPrestamoDetalleServiceImplTest {

    @Mock
    private SolicitudPrestamoDetalleRepository detalleRepository;

    @Mock
    private SolicitudPrestamoRepository solicitudRepository;

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private SolicitudPrestamoDetalleMapper detalleMapper;

    @InjectMocks
    private SolicitudPrestamoDetalleServiceImpl detalleService;

    @Test
    @DisplayName("Should return details for a given solicitud ID")
    void shouldReturnDetailsBySolicitud() {
        SolicitudPrestamoDetalle entity = SolicitudPrestamoDetalle.builder().id(1L).build();
        SolicitudPrestamoDetalleItemResponse response = SolicitudPrestamoDetalleItemResponse.builder()
                .id(1L)
                .idSolicitud(100L)
                .idLibro(50L)
                .tituloObra("Don Quijote")
                .cantidadSolicitada(2)
                .build();

        when(detalleRepository.findBySolicitudPrestamoIdAndBorradoLogicoFalse(100L)).thenReturn(List.of(entity));
        when(detalleMapper.toResponse(entity)).thenReturn(response);

        List<SolicitudPrestamoDetalleItemResponse> result = detalleService.obtenerPorSolicitud(100L);

        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("Should add detail when valid")
    void shouldAddDetailWhenValid() {
        SolicitudPrestamoDetalleRequest request = SolicitudPrestamoDetalleRequest.builder()
                .idSolicitud(JsonNullable.of(100L))
                .idLibro(JsonNullable.of(50L))
                .cantidadSolicitada(JsonNullable.of(2))
                .build();

        SolicitudPrestamo solicitud = SolicitudPrestamo.builder().id(100L).borradoLogico(false).build();
        Obra obra = Obra.builder().id(5L).titulo("Cien Años de Soledad").build();
        Libro libro = Libro.builder().id(50L).obra(obra).borradoLogico(false).build();
        SolicitudPrestamoDetalle entity = SolicitudPrestamoDetalle.builder().id(1L).build();
        SolicitudPrestamoDetalleItemResponse response = SolicitudPrestamoDetalleItemResponse.builder().id(1L).build();

        when(solicitudRepository.findById(100L)).thenReturn(Optional.of(solicitud));
        when(libroRepository.findById(50L)).thenReturn(Optional.of(libro));
        when(detalleMapper.toEntity(request)).thenReturn(entity);
        when(detalleRepository.save(any(SolicitudPrestamoDetalle.class))).thenReturn(entity);
        when(detalleMapper.toResponse(entity)).thenReturn(response);

        SolicitudPrestamoDetalleItemResponse result = detalleService.agregarDetalle(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw when book is inactive on add detail")
    void shouldThrowWhenBookInactive() {
        SolicitudPrestamoDetalleRequest request = SolicitudPrestamoDetalleRequest.builder()
                .idSolicitud(JsonNullable.of(100L))
                .idLibro(JsonNullable.of(50L))
                .build();

        SolicitudPrestamo solicitud = SolicitudPrestamo.builder().id(100L).borradoLogico(false).build();
        Libro libro = Libro.builder().id(50L).borradoLogico(true).build();

        when(solicitudRepository.findById(100L)).thenReturn(Optional.of(solicitud));
        when(libroRepository.findById(50L)).thenReturn(Optional.of(libro));

        assertThatThrownBy(() -> detalleService.agregarDetalle(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("inactivo");
    }

    @Test
    @DisplayName("Should activate and deactivate detail")
    void shouldChangeEstado() {
        when(detalleRepository.existsById(1L)).thenReturn(true);

        detalleService.cambiarEstado(1L, true);
        verify(detalleRepository).activar(1L);

        detalleService.cambiarEstado(1L, false);
        verify(detalleRepository).desactivar(1L);
    }
}
