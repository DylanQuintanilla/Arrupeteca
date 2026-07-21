package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.dto.response.PlantaResponse;
import com.arrupeteca.dto.response.SalonResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.SalonMapper;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.repository.SalonRepository;
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

class SalonServiceImplTest extends BaseServiceTest {

    @Mock
    private SalonRepository salonRepository;

    @Mock
    private SalonMapper salonMapper;

    @InjectMocks
    private SalonServiceImpl salonService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of SalonResponse")
    void obtenerTodosActivos() {
        Salon entity = Salon.builder().id(1L).nombre("Test").build();
        Page<Salon> page = new PageImpl<>(List.of(entity));
        SalonResponse response = new SalonResponse(1L, "Test", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false)));

        when(salonRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(salonMapper.toResponse(entity)).thenReturn(response);

        Page<SalonResponse> result = salonService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return SalonResponse when found and active")
    void obtenerPorIdActivoSuccess() {
        SalonResponse response = new SalonResponse(1L, "Test", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false)));
        Salon entity = Salon.builder().id(1L).nombre("Test").build();
        
        when(salonRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(salonMapper.toResponse(entity)).thenReturn(response);

        SalonResponse result = salonService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(salonRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salonService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un salón activo con el ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return page of SalonResponse")
    void obtenerTodos() {
        Salon entity = Salon.builder().id(1L).nombre("Test").build();
        Page<Salon> page = new PageImpl<>(List.of(entity));
        SalonResponse response = new SalonResponse(1L, "Test", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false)));

        when(salonRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(salonMapper.toResponse(entity)).thenReturn(response);

        Page<SalonResponse> result = salonService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should return SalonResponse when found")
    void obtenerPorIdSuccess() {
        Salon entity = Salon.builder().id(1L).nombre("Test").build();
        SalonResponse response = new SalonResponse(1L, "Test", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false)));
        
        when(salonRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(salonMapper.toResponse(entity)).thenReturn(response);

        SalonResponse result = salonService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(salonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salonService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un salón con el ID: 1");
    }

    @Test
    @DisplayName("buscarPorPlantaId - Should return page of SalonResponse")
    void buscarPorPlantaId() {
        Salon entity = Salon.builder().id(1L).nombre("Test").build();
        Page<Salon> page = new PageImpl<>(List.of(entity));
        SalonResponse response = new SalonResponse(1L, "Test", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false)));

        when(salonRepository.findByPlantaIdAndBorradoLogicoFalse(1L, Pageable.unpaged())).thenReturn(page);
        when(salonMapper.toResponse(entity)).thenReturn(response);

        Page<SalonResponse> result = salonService.buscarPorPlantaId(1L, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("crear - Should create and return SalonResponse")
    void crearSuccess() {
        SalonRequest request = SalonRequest.builder().nombre("Nuevo").idPlanta(1L).build();
        Salon entity = Salon.builder().id(1L).nombre("Nuevo").build();
        Salon savedEntity = Salon.builder().id(1L).nombre("Nuevo").build();
        SalonResponse response = new SalonResponse(1L, "Nuevo", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false)));

        when(salonRepository.existsByNombreIgnoreCaseAndPlantaId("Nuevo", 1L)).thenReturn(false);
        when(salonMapper.toEntity(request)).thenReturn(entity);
        when(salonRepository.save(entity)).thenReturn(savedEntity);
        when(salonMapper.toResponse(savedEntity)).thenReturn(response);

        SalonResponse result = salonService.crear(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists in plant")
    void crearDuplicateName() {
        SalonRequest request = SalonRequest.builder().nombre("Existe").idPlanta(1L).build();

        when(salonRepository.existsByNombreIgnoreCaseAndPlantaId("Existe", 1L)).thenReturn(true);

        assertThatThrownBy(() -> salonService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un salón con ese nombre en la planta seleccionada");
    }

    @Test
    @DisplayName("actualizar - Should update and return SalonResponse")
    void actualizarSuccess() {
        Long id = 1L;
        SalonRequest request = SalonRequest.builder().nombre("Actualizado").idPlanta(1L).build();
        Salon existingEntity = Salon.builder().id(1L).nombre("Original").build();
        SalonResponse response = new SalonResponse(id, "Actualizado", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false)));

        when(salonRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(salonRepository.existsByNombreIgnoreCaseAndPlantaId("Actualizado", 1L)).thenReturn(false);
        when(salonRepository.save(existingEntity)).thenReturn(existingEntity);
        when(salonMapper.toResponse(existingEntity)).thenReturn(response);

        SalonResponse result = salonService.actualizar(id, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        Long id = 1L;
        SalonRequest request = SalonRequest.builder().nombre("Cualquiera").idPlanta(1L).build();

        when(salonRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salonService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un salón con el ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when updating to existing name in plant")
    void actualizarDuplicateName() {
        Long id = 1L;
        SalonRequest request = SalonRequest.builder().nombre("Existe").idPlanta(1L).build();
        Salon existingEntity = Salon.builder().id(1L).nombre("Original").build();

        when(salonRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(salonRepository.existsByNombreIgnoreCaseAndPlantaId("Existe", 1L)).thenReturn(true);

        assertThatThrownBy(() -> salonService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro salón con ese nombre en la planta seleccionada");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate when state is true")
    void cambiarEstadoActivar() {
        when(salonRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        salonService.cambiarEstado(1L, true);

        verify(salonRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate when state is false")
    void cambiarEstadoDesactivar() {
        when(salonRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        salonService.cambiarEstado(1L, false);

        verify(salonRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(salonRepository.countByIdIncludeDeleted(1L)).thenReturn(0L);

        assertThatThrownBy(() -> salonService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un salón con el ID: 1");
    }
}
