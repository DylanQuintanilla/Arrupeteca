package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.detail.PlantaDetalleResponse;
import com.arrupeteca.dto.response.summary.PlantaResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PlantaMapper;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.repository.PlantaRepository;
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
class PlantaServiceImplTest {

    @Mock
    private PlantaRepository plantaRepository;

    @Mock
    private PlantaMapper plantaMapper;

    @InjectMocks
    private PlantaServiceImpl plantaService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of Resumen")
    void obtenerTodosActivos() {
        Planta entity = Planta.builder().id(1L).nombre("Planta 1").build();
        Page<Planta> page = new PageImpl<>(List.of(entity));
        PlantaResumen resumen = new PlantaResumen(1L, "Planta 1", false, "Edificio A");

        when(plantaRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(plantaMapper.toResumen(entity)).thenReturn(resumen);

        Page<PlantaResumen> result = plantaService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return detail when exists and active")
    void obtenerPorIdActivoSuccess() {
        Planta entity = Planta.builder().id(1L).nombre("Planta 1").build();
        PlantaDetalleResponse detail = new PlantaDetalleResponse(1L, "Planta 1", false, null, null, null, null, null);

        when(plantaRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(plantaMapper.toDetalleResponse(entity)).thenReturn(detail);

        PlantaDetalleResponse result = plantaService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found or inactive")
    void obtenerPorIdActivoNotFound() {
        when(plantaRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> plantaService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe una planta activa con el ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return all plants")
    void obtenerTodos() {
        Planta entity = Planta.builder().id(1L).nombre("Planta 1").build();
        Page<Planta> page = new PageImpl<>(List.of(entity));
        PlantaResumen resumen = new PlantaResumen(1L, "Planta 1", false, "Edificio A");

        when(plantaRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        when(plantaMapper.toResumen(entity)).thenReturn(resumen);

        Page<PlantaResumen> result = plantaService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId - Should return detail when exists")
    void obtenerPorIdSuccess() {
        Planta entity = Planta.builder().id(1L).nombre("Planta 1").build();
        PlantaDetalleResponse detail = new PlantaDetalleResponse(1L, "Planta 1", false, null, null, null, null, null);

        when(plantaRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(plantaMapper.toDetalleResponse(entity)).thenReturn(detail);

        PlantaDetalleResponse result = plantaService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(plantaRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> plantaService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe una planta con el ID: 1");
    }

    @Test
    @DisplayName("buscarPorEdificioId - Should return plants for edificio")
    void buscarPorEdificioId() {
        Planta entity = Planta.builder().id(1L).nombre("Planta 1").build();
        Page<Planta> page = new PageImpl<>(List.of(entity));
        PlantaResumen resumen = new PlantaResumen(1L, "Planta 1", false, "Edificio A");

        when(plantaRepository.findByEdificioIdAndBorradoLogicoFalse(eq(1L), any(Pageable.class))).thenReturn(page);
        when(plantaMapper.toResumen(entity)).thenReturn(resumen);

        Page<PlantaResumen> result = plantaService.buscarPorEdificioId(1L, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("crear - Should create planta")
    void crearSuccess() {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Nuevo");
        request.setIdEdificio(1L);

        Planta entity = new Planta();
        Planta saved = Planta.builder().id(1L).nombre("Nuevo").build();
        PlantaDetalleResponse detail = new PlantaDetalleResponse(1L, "Nuevo", false, null, null, null, null, null);

        when(plantaRepository.existsByNombreAndEdificioId("Nuevo", 1L)).thenReturn(false);
        when(plantaMapper.toEntity(request)).thenReturn(entity);
        when(plantaRepository.save(entity)).thenReturn(saved);
        when(plantaMapper.toDetalleResponse(saved)).thenReturn(detail);

        PlantaDetalleResponse result = plantaService.crear(request);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists in edificio")
    void crearDuplicate() {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Existente");
        request.setIdEdificio(1L);

        when(plantaRepository.existsByNombreAndEdificioId("Existente", 1L)).thenReturn(true);

        assertThatThrownBy(() -> plantaService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una planta con ese nombre en el edificio seleccionado");
    }

    @Test
    @DisplayName("actualizar - Should update planta")
    void actualizarSuccess() {
        Long id = 1L;
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Actualizado");
        request.setIdEdificio(1L);

        Planta existing = Planta.builder().id(id).nombre("Antiguo").build();
        PlantaDetalleResponse detail = new PlantaDetalleResponse(id, "Actualizado", false, null, null, null, null, null);

        when(plantaRepository.findById(id)).thenReturn(Optional.of(existing));
        when(plantaRepository.existsByNombreAndEdificioId("Actualizado", 1L)).thenReturn(false);
        when(plantaRepository.save(existing)).thenReturn(existing);
        when(plantaMapper.toDetalleResponse(existing)).thenReturn(detail);

        PlantaDetalleResponse result = plantaService.actualizar(id, request);

        assertThat(result).isEqualTo(detail);
        verify(plantaMapper).updateEntity(request, existing);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when planta not found")
    void actualizarNotFound() {
        when(plantaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> plantaService.actualizar(1L, new PlantaRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe una planta con el ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when new name exists in edificio")
    void actualizarDuplicate() {
        Long id = 1L;
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Existente");
        request.setIdEdificio(1L);

        Planta existing = Planta.builder().id(id).nombre("Antiguo").build();

        when(plantaRepository.findById(id)).thenReturn(Optional.of(existing));
        when(plantaRepository.existsByNombreAndEdificioId("Existente", 1L)).thenReturn(true);

        assertThatThrownBy(() -> plantaService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otra planta con ese nombre en el edificio seleccionado");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate planta")
    void cambiarEstadoActivar() {
        when(plantaRepository.existsById(1L)).thenReturn(true);

        plantaService.cambiarEstado(1L, true);

        verify(plantaRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate planta")
    void cambiarEstadoDesactivar() {
        when(plantaRepository.existsById(1L)).thenReturn(true);

        plantaService.cambiarEstado(1L, false);

        verify(plantaRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(plantaRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> plantaService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe una planta con el ID: 1");
    }
}
