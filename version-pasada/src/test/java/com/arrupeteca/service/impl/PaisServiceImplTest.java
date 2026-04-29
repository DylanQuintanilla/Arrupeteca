package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.summary.PaisResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PaisMapper;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.repository.PaisRepository;
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
class PaisServiceImplTest {

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private PaisMapper paisMapper;

    @InjectMocks
    private PaisServiceImpl paisService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of Resumen")
    void obtenerTodosActivos() {
        Pais entity = Pais.builder().id(1L).nombre("Test").build();
        Page<Pais> page = new PageImpl<>(List.of(entity));
        PaisResumen resumen = new PaisResumen(1L, "Test", false);

        when(paisRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(paisMapper.toResumen(entity)).thenReturn(resumen);

        Page<PaisResumen> result = paisService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return Resumen when found and active")
    void obtenerPorIdActivoSuccess() {
        PaisResumen resumen = new PaisResumen(1L, "Test", false);
        Pais entity = Pais.builder().id(1L).nombre("Test").build();
        
        when(paisRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(paisMapper.toResumen(entity)).thenReturn(resumen);

        PaisResumen result = paisService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(paisRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paisService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un país activo con ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return page of Resumen")
    void obtenerTodos() {
        Pais entity = Pais.builder().id(1L).nombre("Test").build();
        Page<Pais> page = new PageImpl<>(List.of(entity));
        PaisResumen resumen = new PaisResumen(1L, "Test", false);
        
        when(paisRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        when(paisMapper.toResumen(entity)).thenReturn(resumen);

        Page<PaisResumen> result = paisService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorId - Should return Resumen when found")
    void obtenerPorIdSuccess() {
        Pais entity = Pais.builder().id(1L).nombre("Test").build();
        PaisResumen resumen = new PaisResumen(1L, "Test", false);
        
        when(paisRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(paisMapper.toResumen(entity)).thenReturn(resumen);

        PaisResumen result = paisService.obtenerPorId(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(paisRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paisService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un país con ID: 1");
    }

    @Test
    @DisplayName("crear - Should create and return Resumen")
    void crearSuccess() {
        PaisRequest request = new PaisRequest();
        request.setNombre("Nuevo");
        Pais entity = new Pais();
        Pais savedEntity = Pais.builder().id(1L).nombre("Nuevo").build();
        PaisResumen resumen = new PaisResumen(1L, "Nuevo", false);

        when(paisRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(paisMapper.toEntity(request)).thenReturn(entity);
        when(paisRepository.save(entity)).thenReturn(savedEntity);
        when(paisMapper.toResumen(savedEntity)).thenReturn(resumen);

        PaisResumen result = paisService.crear(request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists")
    void crearDuplicateName() {
        PaisRequest request = new PaisRequest();
        request.setNombre("Existe");

        when(paisRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> paisService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un país con el nombre: Existe");
    }

    @Test
    @DisplayName("actualizar - Should update and return Resumen")
    void actualizarSuccess() {
        Long id = 1L;
        PaisRequest request = new PaisRequest();
        request.setNombre("Actualizado");
        Pais existingEntity = Pais.builder().id(1L).nombre("Original").build();
        PaisResumen resumen = new PaisResumen(id, "Actualizado", false);

        when(paisRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(paisRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(paisRepository.save(existingEntity)).thenReturn(existingEntity);
        when(paisMapper.toResumen(existingEntity)).thenReturn(resumen);

        PaisResumen result = paisService.actualizar(id, request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        Long id = 1L;
        PaisRequest request = new PaisRequest();

        when(paisRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paisService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un país con ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when updating to existing name")
    void actualizarDuplicateName() {
        Long id = 1L;
        PaisRequest request = new PaisRequest();
        request.setNombre("Existe");
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
        when(paisRepository.existsById(1L)).thenReturn(true);

        paisService.cambiarEstado(1L, true);

        verify(paisRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate when state is false")
    void cambiarEstadoDesactivar() {
        when(paisRepository.existsById(1L)).thenReturn(true);

        paisService.cambiarEstado(1L, false);

        verify(paisRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(paisRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> paisService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un país con ID: 1");
    }
}
