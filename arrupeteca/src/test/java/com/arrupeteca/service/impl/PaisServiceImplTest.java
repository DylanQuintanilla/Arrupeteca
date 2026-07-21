package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.PaisResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PaisMapper;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.repository.PaisRepository;
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

class PaisServiceImplTest extends BaseServiceTest {

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private PaisMapper paisMapper;

    @InjectMocks
    private PaisServiceImpl paisService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of PaisResponse")
    void obtenerTodosActivos() {
        Pais entity = Pais.builder().id(1L).nombre("Test").build();
        Page<Pais> page = new PageImpl<>(List.of(entity));
        PaisResponse response = new PaisResponse(1L, "Test", false);

        when(paisRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(paisMapper.toResponse(entity)).thenReturn(response);

        Page<PaisResponse> result = paisService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return PaisResponse when found and active")
    void obtenerPorIdActivoSuccess() {
        PaisResponse response = new PaisResponse(1L, "Test", false);
        Pais entity = Pais.builder().id(1L).nombre("Test").build();
        
        when(paisRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(paisMapper.toResponse(entity)).thenReturn(response);

        PaisResponse result = paisService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(paisRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paisService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un país activo con ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return page of PaisResponse")
    void obtenerTodos() {
        Pais entity = Pais.builder().id(1L).nombre("Test").build();
        Page<Pais> page = new PageImpl<>(List.of(entity));
        PaisResponse response = new PaisResponse(1L, "Test", false);
        
        when(paisRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(paisMapper.toResponse(entity)).thenReturn(response);

        Page<PaisResponse> result = paisService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should return PaisResponse when found")
    void obtenerPorIdSuccess() {
        Pais entity = Pais.builder().id(1L).nombre("Test").build();
        PaisResponse response = new PaisResponse(1L, "Test", false);
        
        when(paisRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(paisMapper.toResponse(entity)).thenReturn(response);

        PaisResponse result = paisService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(paisRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paisService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un país con ID: 1");
    }

    @Test
    @DisplayName("crear - Should create and return PaisResponse")
    void crearSuccess() {
        PaisRequest request = PaisRequest.builder().nombre("Nuevo").build();
        Pais entity = Pais.builder().id(1L).nombre("Nuevo").build();
        Pais savedEntity = Pais.builder().id(1L).nombre("Nuevo").build();
        PaisResponse response = new PaisResponse(1L, "Nuevo", false);

        when(paisRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(paisMapper.toEntity(request)).thenReturn(entity);
        when(paisRepository.save(entity)).thenReturn(savedEntity);
        when(paisMapper.toResponse(savedEntity)).thenReturn(response);

        PaisResponse result = paisService.crear(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists")
    void crearDuplicateName() {
        PaisRequest request = PaisRequest.builder().nombre("Existe").build();

        when(paisRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> paisService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un país con el nombre: Existe");
    }

    @Test
    @DisplayName("actualizar - Should update and return PaisResponse")
    void actualizarSuccess() {
        Long id = 1L;
        PaisRequest request = PaisRequest.builder().nombre("Actualizado").build();
        Pais existingEntity = Pais.builder().id(1L).nombre("Original").build();
        PaisResponse response = new PaisResponse(id, "Actualizado", false);

        when(paisRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(paisRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(paisRepository.save(existingEntity)).thenReturn(existingEntity);
        when(paisMapper.toResponse(existingEntity)).thenReturn(response);

        PaisResponse result = paisService.actualizar(id, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        Long id = 1L;
        PaisRequest request = PaisRequest.builder().nombre("Cualquiera").build();

        when(paisRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paisService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un país con ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when updating to existing name")
    void actualizarDuplicateName() {
        Long id = 1L;
        PaisRequest request = PaisRequest.builder().nombre("Existe").build();
        Pais existingEntity = Pais.builder().id(1L).nombre("Original").build();

        when(paisRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(paisRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> paisService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro país con el nombre: Existe");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate when state is true")
    void cambiarEstadoActivar() {
        when(paisRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        paisService.cambiarEstado(1L, true);

        verify(paisRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate when state is false")
    void cambiarEstadoDesactivar() {
        when(paisRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        paisService.cambiarEstado(1L, false);

        verify(paisRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(paisRepository.countByIdIncludeDeleted(1L)).thenReturn(0L);

        assertThatThrownBy(() -> paisService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un país con ID: 1");
    }
}
