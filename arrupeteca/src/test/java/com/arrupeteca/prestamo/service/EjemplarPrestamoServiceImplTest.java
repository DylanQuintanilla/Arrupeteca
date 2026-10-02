package com.arrupeteca.prestamo.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
import com.arrupeteca.persistence.repository.EstadoFisicoRepository;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.prestamo.dto.request.EjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.EjemplarPrestamoDetalleResponse;
import com.arrupeteca.prestamo.internal.entity.EjemplarPrestamo;
import com.arrupeteca.prestamo.internal.entity.EstadoEjemplarPrestamo;
import com.arrupeteca.prestamo.internal.entity.Prestamo;
import com.arrupeteca.prestamo.internal.mapper.EjemplarPrestamoMapper;
import com.arrupeteca.prestamo.internal.repository.EjemplarPrestamoRepository;
import com.arrupeteca.prestamo.internal.repository.EstadoEjemplarPrestamoRepository;
import com.arrupeteca.prestamo.internal.repository.PrestamoRepository;
import com.arrupeteca.prestamo.internal.service.impl.EjemplarPrestamoServiceImpl;
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
    private EstadoEjemplarPrestamoRepository estadoEjemplarPrestamoRepository;

    @Mock
    private EstadoFisicoRepository estadoFisicoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EjemplarPrestamoMapper ejemplarPrestamoMapper;

    @Mock
    private org.springframework.context.ApplicationEventPublisher events;

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
        EjemplarLibro ejemplar = EjemplarLibro.builder().id(50L).borradoLogico(false).build();
        EstadoEjemplarPrestamo estadoEjemplar = EstadoEjemplarPrestamo.builder().id(1L).nombre("EN_PRESTAMO").build();
        EjemplarPrestamo entity = EjemplarPrestamo.builder().id(1L).build();
        EjemplarPrestamoDetalleResponse response = EjemplarPrestamoDetalleResponse.builder().id(1L).build();

        when(prestamoRepository.findById(100L)).thenReturn(Optional.of(prestamo));
        when(ejemplarLibroRepository.findById(50L)).thenReturn(Optional.of(ejemplar));
        when(estadoEjemplarPrestamoRepository.findById(1L)).thenReturn(Optional.of(estadoEjemplar));
        when(ejemplarPrestamoMapper.toEntity(request)).thenReturn(entity);
        when(ejemplarPrestamoRepository.save(any(EjemplarPrestamo.class))).thenReturn(entity);
        when(ejemplarPrestamoMapper.toDetalleResponse(entity)).thenReturn(response);

        EjemplarPrestamoDetalleResponse result = ejemplarPrestamoService.agregarEjemplar(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should pay mora successfully")
    void shouldPayMora() {
        EjemplarPrestamo entity = EjemplarPrestamo.builder().id(1L).moraCalculada(new java.math.BigDecimal("10.00")).moraPagada(false).build();

        when(ejemplarPrestamoRepository.busquedaPorId(1L, null)).thenReturn(Optional.of(entity));
        when(ejemplarPrestamoRepository.save(any(EjemplarPrestamo.class))).thenReturn(entity);

        ejemplarPrestamoService.pagarMora(1L);

        assertThat(entity.isMoraPagada()).isTrue();
        verify(ejemplarPrestamoRepository).save(entity);
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
