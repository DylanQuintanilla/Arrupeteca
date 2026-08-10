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
import static org.mockito.ArgumentMatchers.eq;
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

        when(plantaRepository.buscarActivosPorNombre(any())).thenReturn(List.of(entity));
        when(plantaMapper.toResponse(entity)).thenReturn(response);

        List<PlantaResponse> result = plantaService.obtenerTodosActivos(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(response);
    }



    @Test
    @DisplayName("obtenerTodos - Should return page of PlantaResponse")
    void obtenerTodos() {
        Planta entity = Planta.builder().id(1L).nombre("Test").build();
        Page<Planta> page = new PageImpl<>(List.of(entity));
        PlantaResponse response = new PlantaResponse(1L, "Test", false, new EdificioResponse(1L, "Edif", false));

        when(plantaRepository.busquedaAdmin(any(), any(), any(), any())).thenReturn(page);
        when(plantaMapper.toResponse(entity)).thenReturn(response);

        Page<PlantaResponse> result = plantaService.obtenerTodos(null, null, null, Pageable.unpaged());

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
                .hasMessageContaining("con ID: 1");
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
                .hasMessageContaining("con ese nombre");
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
                .hasMessageContaining("con ID: 1");
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
                .hasMessageContaining("con ese nombre");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate when state is true")
    void cambiarEstadoActivar() {
        when(plantaRepository.existsById(1L)).thenReturn(true);

        plantaService.cambiarEstado(1L, true);

        verify(plantaRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate when state is false")
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
                .hasMessageContaining("con ID: 1");
    }
}
