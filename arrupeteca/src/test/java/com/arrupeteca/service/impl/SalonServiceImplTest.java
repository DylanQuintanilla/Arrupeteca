package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.detail.SalonDetalleResponse;
import com.arrupeteca.dto.response.summary.SalonResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.SalonMapper;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.repository.SalonRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalonServiceImplTest {

    @Mock
    private SalonRepository salonRepository;

    @Mock
    private SalonMapper salonMapper;

    @InjectMocks
    private SalonServiceImpl salonService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of Resumen")
    void obtenerTodosActivos() {
        Salon entity = Salon.builder().id(1L).nombre("Salon A").build();
        Page<Salon> page = new PageImpl<>(List.of(entity));
        SalonResumen resumen = new SalonResumen(1L, "Salon A", false, "Planta 1");

        when(salonRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(salonMapper.toResumen(entity)).thenReturn(resumen);

        Page<SalonResumen> result = salonService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return detail when exists and active")
    void obtenerPorIdActivoSuccess() {
        Salon entity = Salon.builder().id(1L).nombre("Salon A").build();
        SalonDetalleResponse detail = new SalonDetalleResponse(1L, "Salon A", false, null, null, null, null, null);

        when(salonRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(salonMapper.toDetalleResponse(entity)).thenReturn(detail);

        SalonDetalleResponse result = salonService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found or inactive")
    void obtenerPorIdActivoNotFound() {
        when(salonRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salonService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un salón activo con el ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return all rooms")
    void obtenerTodos() {
        Salon entity = Salon.builder().id(1L).nombre("Salon A").build();
        Page<Salon> page = new PageImpl<>(List.of(entity));
        SalonResumen resumen = new SalonResumen(1L, "Salon A", false, "Planta 1");

        when(salonRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        when(salonMapper.toResumen(entity)).thenReturn(resumen);

        Page<SalonResumen> result = salonService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId - Should return detail when exists")
    void obtenerPorIdSuccess() {
        Salon entity = Salon.builder().id(1L).nombre("Salon A").build();
        SalonDetalleResponse detail = new SalonDetalleResponse(1L, "Salon A", false, null, null, null, null, null);

        when(salonRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(salonMapper.toDetalleResponse(entity)).thenReturn(detail);

        SalonDetalleResponse result = salonService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(salonRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salonService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un salón con el ID: 1");
    }

    @Test
    @DisplayName("buscarPorPlantaId - Should return rooms for planta")
    void buscarPorPlantaId() {
        Salon entity = Salon.builder().id(1L).nombre("Salon A").build();
        Page<Salon> page = new PageImpl<>(List.of(entity));
        SalonResumen resumen = new SalonResumen(1L, "Salon A", false, "Planta 1");

        when(salonRepository.findByPlantaIdAndBorradoLogicoFalse(eq(1L), any(Pageable.class))).thenReturn(page);
        when(salonMapper.toResumen(entity)).thenReturn(resumen);

        Page<SalonResumen> result = salonService.buscarPorPlantaId(1L, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("crear - Should create room")
    void crearSuccess() {
        SalonRequest request = new SalonRequest();
        request.setNombre("Nuevo");
        request.setIdPlanta(1L);

        Salon entity = new Salon();
        Salon saved = Salon.builder().id(1L).nombre("Nuevo").build();
        SalonDetalleResponse detail = new SalonDetalleResponse(1L, "Nuevo", false, null, null, null, null, null);

        when(salonRepository.existsByNombreAndPlantaId("Nuevo", 1L)).thenReturn(false);
        when(salonMapper.toEntity(request)).thenReturn(entity);
        when(salonRepository.save(entity)).thenReturn(saved);
        when(salonMapper.toDetalleResponse(saved)).thenReturn(detail);

        SalonDetalleResponse result = salonService.crear(request);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists in planta")
    void crearDuplicate() {
        SalonRequest request = new SalonRequest();
        request.setNombre("Existente");
        request.setIdPlanta(1L);

        when(salonRepository.existsByNombreAndPlantaId("Existente", 1L)).thenReturn(true);

        assertThatThrownBy(() -> salonService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un salón con ese nombre en la planta seleccionada");
    }

    @Test
    @DisplayName("actualizar - Should update room")
    void actualizarSuccess() {
        Long id = 1L;
        SalonRequest request = new SalonRequest();
        request.setNombre("Actualizado");
        request.setIdPlanta(1L);

        Salon existing = Salon.builder().id(id).nombre("Antiguo").build();
        SalonDetalleResponse detail = new SalonDetalleResponse(id, "Actualizado", false, null, null, null, null, null);

        when(salonRepository.findById(id)).thenReturn(Optional.of(existing));
        when(salonRepository.existsByNombreAndPlantaId("Actualizado", 1L)).thenReturn(false);
        when(salonRepository.save(existing)).thenReturn(existing);
        when(salonMapper.toDetalleResponse(existing)).thenReturn(detail);

        SalonDetalleResponse result = salonService.actualizar(id, request);

        assertThat(result).isEqualTo(detail);
        verify(salonMapper).updateEntity(request, existing);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when room not found")
    void actualizarNotFound() {
        when(salonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salonService.actualizar(1L, new SalonRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un salón con el ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when new name exists in planta")
    void actualizarDuplicate() {
        Long id = 1L;
        SalonRequest request = new SalonRequest();
        request.setNombre("Existente");
        request.setIdPlanta(1L);

        Salon existing = Salon.builder().id(id).nombre("Antiguo").build();

        when(salonRepository.findById(id)).thenReturn(Optional.of(existing));
        when(salonRepository.existsByNombreAndPlantaId("Existente", 1L)).thenReturn(true);

        assertThatThrownBy(() -> salonService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro salón con ese nombre en la planta seleccionada");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate room")
    void cambiarEstadoActivar() {
        when(salonRepository.existsById(1L)).thenReturn(true);

        salonService.cambiarEstado(1L, true);

        verify(salonRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate room")
    void cambiarEstadoDesactivar() {
        when(salonRepository.existsById(1L)).thenReturn(true);

        salonService.cambiarEstado(1L, false);

        verify(salonRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(salonRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> salonService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un salón con el ID: 1");
    }
}
