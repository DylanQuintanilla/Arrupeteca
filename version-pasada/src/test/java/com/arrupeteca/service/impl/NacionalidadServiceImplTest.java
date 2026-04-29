package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.summary.NacionalidadResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.NacionalidadMapper;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.repository.NacionalidadRepository;
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
class NacionalidadServiceImplTest {

    @Mock
    private NacionalidadRepository nacionalidadRepository;

    @Mock
    private NacionalidadMapper nacionalidadMapper;

    @InjectMocks
    private NacionalidadServiceImpl nacionalidadService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of Resumen")
    void obtenerTodosActivos() {
        Nacionalidad entity = Nacionalidad.builder().id(1L).nombre("Salvadoreña").build();
        Page<Nacionalidad> page = new PageImpl<>(List.of(entity));
        NacionalidadResumen resumen = new NacionalidadResumen(1L, "Salvadoreña", false);

        when(nacionalidadRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(nacionalidadMapper.toResumen(entity)).thenReturn(resumen);

        Page<NacionalidadResumen> result = nacionalidadService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return resumen when exists and active")
    void obtenerPorIdActivoSuccess() {
        Nacionalidad entity = Nacionalidad.builder().id(1L).nombre("Salvadoreña").build();
        NacionalidadResumen resumen = new NacionalidadResumen(1L, "Salvadoreña", false);

        when(nacionalidadRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(nacionalidadMapper.toResumen(entity)).thenReturn(resumen);

        NacionalidadResumen result = nacionalidadService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(nacionalidadRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> nacionalidadService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("La nacionalidad con ID 1 no existe o está inactiva");
    }

    @Test
    @DisplayName("obtenerTodos - Should return all")
    void obtenerTodos() {
        Nacionalidad entity = Nacionalidad.builder().id(1L).nombre("Salvadoreña").build();
        Page<Nacionalidad> page = new PageImpl<>(List.of(entity));
        NacionalidadResumen resumen = new NacionalidadResumen(1L, "Salvadoreña", false);

        when(nacionalidadRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        when(nacionalidadMapper.toResumen(entity)).thenReturn(resumen);

        Page<NacionalidadResumen> result = nacionalidadService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId - Should return resumen")
    void obtenerPorIdSuccess() {
        Nacionalidad entity = Nacionalidad.builder().id(1L).nombre("Salvadoreña").build();
        NacionalidadResumen resumen = new NacionalidadResumen(1L, "Salvadoreña", false);

        when(nacionalidadRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(nacionalidadMapper.toResumen(entity)).thenReturn(resumen);

        NacionalidadResumen result = nacionalidadService.obtenerPorId(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(nacionalidadRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> nacionalidadService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("La nacionalidad con ID 1 no existe");
    }

    @Test
    @DisplayName("crear - Should create")
    void crearSuccess() {
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("Nueva");

        Nacionalidad entity = new Nacionalidad();
        Nacionalidad saved = Nacionalidad.builder().id(1L).nombre("Nueva").build();
        NacionalidadResumen resumen = new NacionalidadResumen(1L, "Nueva", false);

        when(nacionalidadRepository.existsByNombreIgnoreCase("Nueva")).thenReturn(false);
        when(nacionalidadMapper.toEntity(request)).thenReturn(entity);
        when(nacionalidadRepository.save(entity)).thenReturn(saved);
        when(nacionalidadMapper.toResumen(saved)).thenReturn(resumen);

        NacionalidadResumen result = nacionalidadService.crear(request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists")
    void crearDuplicate() {
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("Existente");

        when(nacionalidadRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> nacionalidadService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una nacionalidad con el nombre: Existente");
    }

    @Test
    @DisplayName("actualizar - Should update")
    void actualizarSuccess() {
        Long id = 1L;
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("Actualizada");

        Nacionalidad existing = Nacionalidad.builder().id(id).nombre("Antigua").build();
        NacionalidadResumen resumen = new NacionalidadResumen(id, "Actualizada", false);

        when(nacionalidadRepository.findById(id)).thenReturn(Optional.of(existing));
        when(nacionalidadRepository.existsByNombreIgnoreCase("Actualizada")).thenReturn(false);
        when(nacionalidadRepository.save(existing)).thenReturn(existing);
        when(nacionalidadMapper.toResumen(existing)).thenReturn(resumen);

        NacionalidadResumen result = nacionalidadService.actualizar(id, request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        when(nacionalidadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> nacionalidadService.actualizar(1L, new NacionalidadRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("La nacionalidad con ID 1 no existe");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when name duplicate")
    void actualizarDuplicate() {
        Long id = 1L;
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("Existente");

        Nacionalidad existing = Nacionalidad.builder().id(id).nombre("Antigua").build();

        when(nacionalidadRepository.findById(id)).thenReturn(Optional.of(existing));
        when(nacionalidadRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> nacionalidadService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otra nacionalidad con el nombre: Existente");
    }

    @Test
    @DisplayName("cambiarEstado - Should change state")
    void cambiarEstado() {
        when(nacionalidadRepository.existsById(1L)).thenReturn(true);

        nacionalidadService.cambiarEstado(1L, true);
        verify(nacionalidadRepository).activar(1L);

        nacionalidadService.cambiarEstado(1L, false);
        verify(nacionalidadRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(nacionalidadRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> nacionalidadService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("La nacionalidad con ID 1 no existe");
    }
}
