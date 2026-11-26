package com.arrupeteca.prestamo.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.Disponibilidad;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.repository.DisponibilidadRepository;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
import com.arrupeteca.persistence.repository.EstadoFisicoRepository;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.prestamo.dto.request.EjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.EjemplarPrestamoDetalleResponse;
import com.arrupeteca.prestamo.persistence.entity.EjemplarPrestamo;
import com.arrupeteca.prestamo.persistence.entity.EstadoEjemplarPrestamo;
import com.arrupeteca.prestamo.persistence.entity.Prestamo;
import com.arrupeteca.prestamo.mapper.EjemplarPrestamoMapper;
import com.arrupeteca.prestamo.persistence.repository.EjemplarPrestamoRepository;
import com.arrupeteca.prestamo.persistence.repository.EstadoEjemplarPrestamoRepository;
import com.arrupeteca.prestamo.persistence.repository.PrestamoRepository;
import com.arrupeteca.prestamo.service.impl.EjemplarPrestamoServiceImpl;
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
class EjemplarPrestamoServiceImplTest {

    @Mock
    private EjemplarPrestamoRepository ejemplarPrestamoRepository;

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private EjemplarLibroRepository ejemplarLibroRepository;

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @Mock
    private EstadoEjemplarPrestamoRepository estadoEjemplarPrestamoRepository;

    @Mock
    private EstadoFisicoRepository estadoFisicoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EjemplarPrestamoMapper ejemplarPrestamoMapper;

    @InjectMocks
    private EjemplarPrestamoServiceImpl ejemplarPrestamoService;

    @Test
    @DisplayName("Should return ejemplares for a given prestamo ID")
    void shouldReturnByPrestamo() {
        EjemplarPrestamo entity = EjemplarPrestamo.builder().id(1L).build();
        EjemplarPrestamoDetalleResponse response = EjemplarPrestamoDetalleResponse.builder()
                .id(1L)
                .idPrestamo(100L)
                .idEjemplar(50L)
                .codigoEjemplar("EJ-001")
                .build();

        when(ejemplarPrestamoRepository.findByPrestamoIdAndBorradoLogicoFalse(100L)).thenReturn(List.of(entity));
        when(ejemplarPrestamoMapper.toDetalleResponse(entity)).thenReturn(response);

        List<EjemplarPrestamoDetalleResponse> result = ejemplarPrestamoService.obtenerPorPrestamo(100L);

        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("Should add ejemplar when valid")
    void shouldAddEjemplarWhenValid() {
        EjemplarPrestamoRequest request = EjemplarPrestamoRequest.builder()
                .idPrestamo(JsonNullable.of(100L))
                .idEjemplar(JsonNullable.of(50L))
                .idEstadoEjemplarPrestamo(JsonNullable.of(1L))
                .build();

        Prestamo prestamo = Prestamo.builder().id(100L).borradoLogico(false).build();
        Disponibilidad disponible = Disponibilidad.builder().id(1L).nombre("Disponible").build();
        Disponibilidad prestado = Disponibilidad.builder().id(2L).nombre("Prestado").build();
        EjemplarLibro ejemplar = EjemplarLibro.builder().id(50L).borradoLogico(false).disponibilidad(disponible).build();
        EstadoEjemplarPrestamo estadoEjemplar = EstadoEjemplarPrestamo.builder().id(1L).nombre("EN_PRESTAMO").build();
        EjemplarPrestamo entity = EjemplarPrestamo.builder().id(1L).build();
        EjemplarPrestamoDetalleResponse response = EjemplarPrestamoDetalleResponse.builder().id(1L).build();

        when(prestamoRepository.findById(100L)).thenReturn(Optional.of(prestamo));
        when(ejemplarLibroRepository.findById(50L)).thenReturn(Optional.of(ejemplar));
        when(ejemplarPrestamoRepository.existsByPrestamoIdAndEjemplarIdAndBorradoLogicoFalse(100L, 50L)).thenReturn(false);
        when(ejemplarPrestamoRepository.existePrestamoActivoParaEjemplar(any(), any(), any())).thenReturn(false);
        when(estadoEjemplarPrestamoRepository.findById(1L)).thenReturn(Optional.of(estadoEjemplar));
        when(ejemplarPrestamoMapper.toEntity(request)).thenReturn(entity);
        when(ejemplarPrestamoRepository.saveAndFlush(any(EjemplarPrestamo.class))).thenReturn(entity);
        when(disponibilidadRepository.findFirstByNombreIgnoreCaseAndBorradoLogicoFalse("PRESTADO"))
                .thenReturn(Optional.of(prestado));
        when(ejemplarPrestamoMapper.toDetalleResponse(entity)).thenReturn(response);

        EjemplarPrestamoDetalleResponse result = ejemplarPrestamoService.agregarEjemplar(request);

        assertThat(result).isEqualTo(response);
        assertThat(ejemplar.getDisponibilidad()).isEqualTo(prestado);
        verify(ejemplarLibroRepository).save(ejemplar);
    }

    @Test
    @DisplayName("Should pay mora successfully and return updated detail")
    void shouldPayMora() {
        EjemplarPrestamo entity = EjemplarPrestamo.builder().id(1L).moraCalculada(new java.math.BigDecimal("10.00")).moraPagada(false).build();
        EjemplarPrestamoDetalleResponse detail = EjemplarPrestamoDetalleResponse.builder().id(1L).moraPagada(true).build();

        when(ejemplarPrestamoRepository.busquedaPorId(1L, null)).thenReturn(Optional.of(entity));
        when(ejemplarPrestamoRepository.saveAndFlush(any(EjemplarPrestamo.class))).thenReturn(entity);
        when(ejemplarPrestamoMapper.toDetalleResponse(entity)).thenReturn(detail);

        EjemplarPrestamoDetalleResponse result = ejemplarPrestamoService.pagarMora(1L);

        assertThat(result).isEqualTo(detail);
        assertThat(entity.isMoraPagada()).isTrue();
        verify(ejemplarPrestamoRepository).saveAndFlush(entity);
    }

    @Test
    @DisplayName("Should exempt mora and return updated detail")
    void shouldExemptMora() {
        EjemplarPrestamo entity = EjemplarPrestamo.builder().id(1L).moraCalculada(new java.math.BigDecimal("10.00")).exentoMora(false).build();
        EjemplarPrestamoDetalleResponse detail = EjemplarPrestamoDetalleResponse.builder().id(1L).exentoMora(true).build();

        when(ejemplarPrestamoRepository.busquedaPorId(1L, null)).thenReturn(Optional.of(entity));
        when(ejemplarPrestamoRepository.saveAndFlush(any(EjemplarPrestamo.class))).thenReturn(entity);
        when(ejemplarPrestamoMapper.toDetalleResponse(entity)).thenReturn(detail);

        EjemplarPrestamoDetalleResponse result = ejemplarPrestamoService.exentarMora(1L);

        assertThat(result).isEqualTo(detail);
        assertThat(entity.isExentoMora()).isTrue();
        assertThat(entity.getMoraCalculada()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Should reject adding ejemplar if not Disponible or already loaned")
    void shouldRejectUnavailableOrDuplicateEjemplar() {
        EjemplarPrestamoRequest request = EjemplarPrestamoRequest.builder()
                .idPrestamo(JsonNullable.of(100L))
                .idEjemplar(JsonNullable.of(50L))
                .build();
        Prestamo prestamo = Prestamo.builder().id(100L).borradoLogico(false).build();
        Disponibilidad prestado = Disponibilidad.builder().id(2L).nombre("Prestado").build();
        EjemplarLibro ejemplarPrestado = EjemplarLibro.builder().id(50L).borradoLogico(false).disponibilidad(prestado).build();

        when(prestamoRepository.findById(100L)).thenReturn(Optional.of(prestamo));
        when(ejemplarLibroRepository.findById(50L)).thenReturn(Optional.of(ejemplarPrestado));

        assertThatThrownBy(() -> ejemplarPrestamoService.agregarEjemplar(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("no está disponible");
    }

    @Test
    @DisplayName("Should throw exception when paying mora if already paid")
    void shouldThrowWhenMoraAlreadyPaid() {
        EjemplarPrestamo entity = EjemplarPrestamo.builder().id(1L).moraCalculada(new java.math.BigDecimal("10.00")).moraPagada(true).build();

        when(ejemplarPrestamoRepository.busquedaPorId(1L, null)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> ejemplarPrestamoService.pagarMora(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("ya ha sido pagada");
    }

    @Test
    @DisplayName("Should activate and deactivate")
    void shouldChangeEstado() {
        when(ejemplarPrestamoRepository.existsById(1L)).thenReturn(true);

        ejemplarPrestamoService.cambiarEstado(1L, true);
        verify(ejemplarPrestamoRepository).activar(1L);

        ejemplarPrestamoService.cambiarEstado(1L, false);
        verify(ejemplarPrestamoRepository).desactivar(1L);
    }
}
