package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.dto.response.MuebleResponse;
import com.arrupeteca.dto.response.PlantaResponse;
import com.arrupeteca.dto.response.SalonResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.MuebleMapper;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.repository.MuebleRepository;
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

class MuebleServiceImplTest extends BaseServiceTest {

    @Mock
    private MuebleRepository muebleRepository;

    @Mock
    private MuebleMapper muebleMapper;

    @InjectMocks
    private MuebleServiceImpl muebleService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of MuebleResponse")
    void obtenerTodosActivos() {
        Mueble entity = Mueble.builder().id(1L).nombre("Test").build();
        Page<Mueble> page = new PageImpl<>(List.of(entity));
        MuebleResponse response = new MuebleResponse(1L, "Test", false, new SalonResponse(1L, "Salon", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false))));

        when(muebleRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(muebleMapper.toResponse(entity)).thenReturn(response);

        Page<MuebleResponse> result = muebleService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return MuebleResponse when found and active")
    void obtenerPorIdActivoSuccess() {
        MuebleResponse response = new MuebleResponse(1L, "Test", false, new SalonResponse(1L, "Salon", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false))));
        Mueble entity = Mueble.builder().id(1L).nombre("Test").build();
        
        when(muebleRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(muebleMapper.toResponse(entity)).thenReturn(response);

        MuebleResponse result = muebleService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(muebleRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> muebleService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Mueble activo con ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return page of MuebleResponse")
    void obtenerTodos() {
        Mueble entity = Mueble.builder().id(1L).nombre("Test").build();
        Page<Mueble> page = new PageImpl<>(List.of(entity));
        MuebleResponse response = new MuebleResponse(1L, "Test", false, new SalonResponse(1L, "Salon", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false))));

        when(muebleRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(muebleMapper.toResponse(entity)).thenReturn(response);

        Page<MuebleResponse> result = muebleService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should return MuebleResponse when found")
    void obtenerPorIdSuccess() {
        Mueble entity = Mueble.builder().id(1L).nombre("Test").build();
        MuebleResponse response = new MuebleResponse(1L, "Test", false, new SalonResponse(1L, "Salon", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false))));
        
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(muebleMapper.toResponse(entity)).thenReturn(response);

        MuebleResponse result = muebleService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(muebleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> muebleService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Mueble con ID: 1");
    }

    @Test
    @DisplayName("crear - Should create and return MuebleResponse")
    void crearSuccess() {
        MuebleRequest request = MuebleRequest.builder().nombre("Nuevo").idSalon(1L).build();
        Mueble entity = Mueble.builder().id(1L).nombre("Nuevo").build();
        Mueble savedEntity = Mueble.builder().id(1L).nombre("Nuevo").build();
        MuebleResponse response = new MuebleResponse(1L, "Nuevo", false, new SalonResponse(1L, "Salon", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false))));

        when(muebleRepository.existsByNombreIgnoreCaseAndSalonId("Nuevo", 1L)).thenReturn(false);
        when(muebleMapper.toEntity(request)).thenReturn(entity);
        when(muebleRepository.save(entity)).thenReturn(savedEntity);
        when(muebleMapper.toResponse(savedEntity)).thenReturn(response);

        MuebleResponse result = muebleService.crear(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists in salon")
    void crearDuplicateName() {
        MuebleRequest request = MuebleRequest.builder().nombre("Existe").idSalon(1L).build();

        when(muebleRepository.existsByNombreIgnoreCaseAndSalonId("Existe", 1L)).thenReturn(true);

        assertThatThrownBy(() -> muebleService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un mueble con ese nombre en el salón seleccionado");
    }

    @Test
    @DisplayName("actualizar - Should update and return MuebleResponse")
    void actualizarSuccess() {
        Long id = 1L;
        MuebleRequest request = MuebleRequest.builder().nombre("Actualizado").idSalon(1L).build();
        Mueble existingEntity = Mueble.builder().id(1L).nombre("Original").build();
        MuebleResponse response = new MuebleResponse(id, "Actualizado", false, new SalonResponse(1L, "Salon", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false))));

        when(muebleRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(muebleRepository.existsByNombreIgnoreCaseAndSalonId("Actualizado", 1L)).thenReturn(false);
        when(muebleRepository.save(existingEntity)).thenReturn(existingEntity);
        when(muebleMapper.toResponse(existingEntity)).thenReturn(response);

        MuebleResponse result = muebleService.actualizar(id, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        Long id = 1L;
        MuebleRequest request = MuebleRequest.builder().nombre("Cualquiera").idSalon(1L).build();

        when(muebleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> muebleService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Mueble con ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when updating to existing name in salon")
    void actualizarDuplicateName() {
        Long id = 1L;
        MuebleRequest request = MuebleRequest.builder().nombre("Existe").idSalon(1L).build();
        Mueble existingEntity = Mueble.builder().id(1L).nombre("Original").build();

        when(muebleRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(muebleRepository.existsByNombreIgnoreCaseAndSalonId("Existe", 1L)).thenReturn(true);

        assertThatThrownBy(() -> muebleService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro mueble con ese nombre en el salón seleccionado");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate when state is true")
    void cambiarEstadoActivar() {
        when(muebleRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        muebleService.cambiarEstado(1L, true);

        verify(muebleRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate when state is false")
    void cambiarEstadoDesactivar() {
        when(muebleRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        muebleService.cambiarEstado(1L, false);

        verify(muebleRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(muebleRepository.countByIdIncludeDeleted(1L)).thenReturn(0L);

        assertThatThrownBy(() -> muebleService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Mueble con ID: 1");
    }
}
