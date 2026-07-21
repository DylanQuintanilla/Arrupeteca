package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.dto.response.PlantaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PlantaMapper;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.repository.PlantaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

class PlantaServiceImplTest extends BaseServiceTest {

    @Mock
    private PlantaRepository plantaRepository;

    @Mock
    private PlantaMapper plantaMapper;

    @InjectMocks
    private PlantaServiceImpl plantaService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of PlantaResponse")
    void obtenerTodosActivos() {
        Planta entity = Planta.builder().id(1L).nombre("Test").build();
        Page<Planta> page = new PageImpl<>(List.of(entity));
        PlantaResponse response = new PlantaResponse(1L, "Test", false, new EdificioResponse(1L, "Edif", false));

        when(plantaRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(plantaMapper.toResponse(entity)).thenReturn(response);

        Page<PlantaResponse> result = plantaService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return PlantaResponse when found and active")
    void obtenerPorIdActivoSuccess() {
        PlantaResponse response = new PlantaResponse(1L, "Test", false, new EdificioResponse(1L, "Edif", false));
        Planta entity = Planta.builder().id(1L).nombre("Test").build();
        
        when(plantaRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(plantaMapper.toResponse(entity)).thenReturn(response);

        PlantaResponse result = plantaService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(plantaRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> plantaService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe una planta activa con el ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return page of PlantaResponse")
    void obtenerTodos() {
        Planta entity = Planta.builder().id(1L).nombre("Test").build();
        Page<Planta> page = new PageImpl<>(List.of(entity));
        PlantaResponse response = new PlantaResponse(1L, "Test", false, new EdificioResponse(1L, "Edif", false));

        when(plantaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(plantaMapper.toResponse(entity)).thenReturn(response);

        Page<PlantaResponse> result = plantaService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should return PlantaResponse when found")
    void obtenerPorIdSuccess() {
        Planta entity = Planta.builder().id(1L).nombre("Test").build();
        PlantaResponse response = new PlantaResponse(1L, "Test", false, new EdificioResponse(1L, "Edif", false));
        
        when(plantaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(plantaMapper.toResponse(entity)).thenReturn(response);

        PlantaResponse result = plantaService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(plantaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> plantaService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe una planta con el ID: 1");
    }

    @Test
    @DisplayName("buscarPorEdificioId - Should return page of PlantaResponse")
    void buscarPorEdificioId() {
        Planta entity = Planta.builder().id(1L).nombre("Test").build();
        Page<Planta> page = new PageImpl<>(List.of(entity));
        PlantaResponse response = new PlantaResponse(1L, "Test", false, new EdificioResponse(1L, "Edif", false));

        when(plantaRepository.findByEdificioIdAndBorradoLogicoFalse(1L, Pageable.unpaged())).thenReturn(page);
        when(plantaMapper.toResponse(entity)).thenReturn(response);

        Page<PlantaResponse> result = plantaService.buscarPorEdificioId(1L, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("crear - Should create and return PlantaResponse")
    void crearSuccess() {
        PlantaRequest request = PlantaRequest.builder().nombre("Nuevo").idEdificio(1L).build();
        Planta entity = Planta.builder().id(1L).nombre("Nuevo").build();
        Planta savedEntity = Planta.builder().id(1L).nombre("Nuevo").build();
        PlantaResponse response = new PlantaResponse(1L, "Nuevo", false, new EdificioResponse(1L, "Edif", false));

        when(plantaRepository.existsByNombreIgnoreCaseAndEdificioId("Nuevo", 1L)).thenReturn(false);
        when(plantaMapper.toEntity(request)).thenReturn(entity);
        when(plantaRepository.save(entity)).thenReturn(savedEntity);
        when(plantaMapper.toResponse(savedEntity)).thenReturn(response);

        PlantaResponse result = plantaService.crear(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists in building")
    void crearDuplicateName() {
        PlantaRequest request = PlantaRequest.builder().nombre("Existe").idEdificio(1L).build();

        when(plantaRepository.existsByNombreIgnoreCaseAndEdificioId("Existe", 1L)).thenReturn(true);

        assertThatThrownBy(() -> plantaService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una planta con ese nombre en el edificio seleccionado");
    }

    @Test
    @DisplayName("actualizar - Should update and return PlantaResponse")
    void actualizarSuccess() {
        Long id = 1L;
        PlantaRequest request = PlantaRequest.builder().nombre("Actualizado").idEdificio(1L).build();
        Planta existingEntity = Planta.builder().id(1L).nombre("Original").build();
        PlantaResponse response = new PlantaResponse(id, "Actualizado", false, new EdificioResponse(1L, "Edif", false));

        when(plantaRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(plantaRepository.existsByNombreIgnoreCaseAndEdificioId("Actualizado", 1L)).thenReturn(false);
        when(plantaRepository.save(existingEntity)).thenReturn(existingEntity);
        when(plantaMapper.toResponse(existingEntity)).thenReturn(response);

        PlantaResponse result = plantaService.actualizar(id, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        Long id = 1L;
        PlantaRequest request = PlantaRequest.builder().nombre("Cualquiera").idEdificio(1L).build();

        when(plantaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> plantaService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe una planta con el ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when updating to existing name in building")
    void actualizarDuplicateName() {
        Long id = 1L;
        PlantaRequest request = PlantaRequest.builder().nombre("Existe").idEdificio(1L).build();
        Planta existingEntity = Planta.builder().id(1L).nombre("Original").build();

        when(plantaRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(plantaRepository.existsByNombreIgnoreCaseAndEdificioId("Existe", 1L)).thenReturn(true);

        assertThatThrownBy(() -> plantaService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otra planta con ese nombre en el edificio seleccionado");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate when state is true")
    void cambiarEstadoActivar() {
        when(plantaRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        plantaService.cambiarEstado(1L, true);

        verify(plantaRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate when state is false")
    void cambiarEstadoDesactivar() {
        when(plantaRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        plantaService.cambiarEstado(1L, false);

        verify(plantaRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(plantaRepository.countByIdIncludeDeleted(1L)).thenReturn(0L);

        assertThatThrownBy(() -> plantaService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe una planta con el ID: 1");
    }
}
