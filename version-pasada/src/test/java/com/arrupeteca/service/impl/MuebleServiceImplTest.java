package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.detail.MuebleDetalleResponse;
import com.arrupeteca.dto.response.summary.MuebleResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.MuebleMapper;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.repository.MuebleRepository;
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
class MuebleServiceImplTest {

    @Mock
    private MuebleRepository muebleRepository;

    @Mock
    private MuebleMapper muebleMapper;

    @InjectMocks
    private MuebleServiceImpl muebleService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of Resumen")
    void obtenerTodosActivos() {
        Mueble entity = Mueble.builder().id(1L).nombre("Mueble 1").build();
        Page<Mueble> page = new PageImpl<>(List.of(entity));
        MuebleResumen resumen = new MuebleResumen(1L, "Mueble 1", false, "Salon A");

        when(muebleRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(muebleMapper.toResumen(entity)).thenReturn(resumen);

        Page<MuebleResumen> result = muebleService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return detail when exists and active")
    void obtenerPorIdActivoSuccess() {
        Mueble entity = Mueble.builder().id(1L).nombre("Mueble 1").build();
        MuebleDetalleResponse detail = new MuebleDetalleResponse(1L, "Mueble 1", false, null, null, null, null, null);

        when(muebleRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(muebleMapper.toDetalleResponse(entity)).thenReturn(detail);

        MuebleDetalleResponse result = muebleService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(muebleRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> muebleService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Mueble activo con ID: 1");
    }

    @Test
    @DisplayName("busquedaGlobal - Should return matching muebles")
    void busquedaGlobal() {
        Mueble entity = Mueble.builder().id(1L).nombre("Mueble 1").build();
        Page<Mueble> page = new PageImpl<>(List.of(entity));
        MuebleResumen resumen = new MuebleResumen(1L, "Mueble 1", false, "Salon A");

        when(muebleRepository.busquedaGlobal(eq("palabra"), any(Pageable.class))).thenReturn(page);
        when(muebleMapper.toResumen(entity)).thenReturn(resumen);

        Page<MuebleResumen> result = muebleService.busquedaGlobal("palabra", Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerTodos - Should return all muebles")
    void obtenerTodos() {
        Mueble entity = Mueble.builder().id(1L).nombre("Mueble 1").build();
        Page<Mueble> page = new PageImpl<>(List.of(entity));
        MuebleResumen resumen = new MuebleResumen(1L, "Mueble 1", false, "Salon A");

        when(muebleRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        when(muebleMapper.toResumen(entity)).thenReturn(resumen);

        Page<MuebleResumen> result = muebleService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId - Should return detail")
    void obtenerPorIdSuccess() {
        Mueble entity = Mueble.builder().id(1L).nombre("Mueble 1").build();
        MuebleDetalleResponse detail = new MuebleDetalleResponse(1L, "Mueble 1", false, null, null, null, null, null);

        when(muebleRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(muebleMapper.toDetalleResponse(entity)).thenReturn(detail);

        MuebleDetalleResponse result = muebleService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(muebleRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> muebleService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Mueble con ID: 1");
    }

    @Test
    @DisplayName("busquedaMaestraAdmin - Should return matching muebles for admin")
    void busquedaMaestraAdmin() {
        Mueble entity = Mueble.builder().id(1L).nombre("Mueble 1").build();
        Page<Mueble> page = new PageImpl<>(List.of(entity));
        MuebleResumen resumen = new MuebleResumen(1L, "Mueble 1", false, "Salon A");

        when(muebleRepository.busquedaMaestraAdmin(eq(false), eq(1L), eq("term"), any(Pageable.class))).thenReturn(page);
        when(muebleMapper.toResumen(entity)).thenReturn(resumen);

        Page<MuebleResumen> result = muebleService.busquedaMaestraAdmin(false, 1L, "term", Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("crear - Should create mueble")
    void crearSuccess() {
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Nuevo");
        request.setIdSalon(1L);

        Mueble entity = new Mueble();
        Mueble saved = Mueble.builder().id(1L).nombre("Nuevo").build();
        MuebleDetalleResponse detail = new MuebleDetalleResponse(1L, "Nuevo", false, null, null, null, null, null);

        when(muebleRepository.existePorNombreYSalonId("Nuevo", 1L)).thenReturn(false);
        when(muebleMapper.toEntity(request)).thenReturn(entity);
        when(muebleRepository.save(entity)).thenReturn(saved);
        when(muebleMapper.toDetalleResponse(saved)).thenReturn(detail);

        MuebleDetalleResponse result = muebleService.crear(request);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("crear - Should throw exception when duplicate name in salon")
    void crearDuplicate() {
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Existente");
        request.setIdSalon(1L);

        when(muebleRepository.existePorNombreYSalonId("Existente", 1L)).thenReturn(true);

        assertThatThrownBy(() -> muebleService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un mueble con ese nombre en el salón seleccionado");
    }

    @Test
    @DisplayName("actualizar - Should update mueble")
    void actualizarSuccess() {
        Long id = 1L;
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Actualizado");
        request.setIdSalon(1L);

        Mueble existing = Mueble.builder().id(id).nombre("Antiguo").build();
        MuebleDetalleResponse detail = new MuebleDetalleResponse(id, "Actualizado", false, null, null, null, null, null);

        when(muebleRepository.findById(id)).thenReturn(Optional.of(existing));
        when(muebleRepository.existePorNombreYSalonId("Actualizado", 1L)).thenReturn(false);
        when(muebleRepository.save(existing)).thenReturn(existing);
        when(muebleMapper.toDetalleResponse(existing)).thenReturn(detail);

        MuebleDetalleResponse result = muebleService.actualizar(id, request);

        assertThat(result).isEqualTo(detail);
        verify(muebleMapper).updateEntity(request, existing);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        when(muebleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> muebleService.actualizar(1L, new MuebleRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Mueble con ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when duplicate name in salon")
    void actualizarDuplicate() {
        Long id = 1L;
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Existente");
        request.setIdSalon(1L);

        Mueble existing = Mueble.builder().id(id).nombre("Antiguo").build();

        when(muebleRepository.findById(id)).thenReturn(Optional.of(existing));
        when(muebleRepository.existePorNombreYSalonId("Existente", 1L)).thenReturn(true);

        assertThatThrownBy(() -> muebleService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro mueble con ese nombre en el salón seleccionado");
    }

    @Test
    @DisplayName("cambiarEstado - Should change state")
    void cambiarEstado() {
        when(muebleRepository.existsById(1L)).thenReturn(true);

        muebleService.cambiarEstado(1L, true);
        verify(muebleRepository).activar(1L);

        muebleService.cambiarEstado(1L, false);
        verify(muebleRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(muebleRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> muebleService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Mueble con ID: 1");
    }
}
