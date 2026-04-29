package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.summary.TipoAutoriaResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.TipoAutoriaMapper;
import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.repository.TipoAutoriaRepository;
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
class TipoAutoriaServiceImplTest {

    @Mock
    private TipoAutoriaRepository tipoAutoriaRepository;

    @Mock
    private TipoAutoriaMapper tipoAutoriaMapper;

    @InjectMocks
    private TipoAutoriaServiceImpl tipoAutoriaService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of Resumen")
    void obtenerTodosActivos() {
        TipoAutoria entity = TipoAutoria.builder().id(1L).nombre("Test").build();
        Page<TipoAutoria> page = new PageImpl<>(List.of(entity));
        TipoAutoriaResumen resumen = new TipoAutoriaResumen(1L, "Test", false);

        when(tipoAutoriaRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(tipoAutoriaMapper.toResumen(entity)).thenReturn(resumen);

        Page<TipoAutoriaResumen> result = tipoAutoriaService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return Resumen when found and active")
    void obtenerPorIdActivoSuccess() {
        TipoAutoria entity = TipoAutoria.builder().id(1L).nombre("Test").build();
        TipoAutoriaResumen resumen = new TipoAutoriaResumen(1L, "Test", false);
        
        when(tipoAutoriaRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(tipoAutoriaMapper.toResumen(entity)).thenReturn(resumen);

        TipoAutoriaResumen result = tipoAutoriaService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(tipoAutoriaRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoAutoriaService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un tipo de autoría activo con ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return page of Resumen")
    void obtenerTodos() {
        TipoAutoria entity = TipoAutoria.builder().id(1L).nombre("Test").build();
        Page<TipoAutoria> page = new PageImpl<>(List.of(entity));
        TipoAutoriaResumen resumen = new TipoAutoriaResumen(1L, "Test", false);
        
        when(tipoAutoriaRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        when(tipoAutoriaMapper.toResumen(entity)).thenReturn(resumen);

        Page<TipoAutoriaResumen> result = tipoAutoriaService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorId - Should return Resumen when found")
    void obtenerPorIdSuccess() {
        TipoAutoria entity = TipoAutoria.builder().id(1L).nombre("Test").build();
        TipoAutoriaResumen resumen = new TipoAutoriaResumen(1L, "Test", false);
        
        when(tipoAutoriaRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(tipoAutoriaMapper.toResumen(entity)).thenReturn(resumen);

        TipoAutoriaResumen result = tipoAutoriaService.obtenerPorId(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(tipoAutoriaRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoAutoriaService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un tipo de autoría con ID: 1");
    }

    @Test
    @DisplayName("crear - Should create and return Resumen")
    void crearSuccess() {
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("Nuevo");
        TipoAutoria entity = new TipoAutoria();
        TipoAutoria savedEntity = TipoAutoria.builder().id(1L).nombre("Nuevo").build();
        TipoAutoriaResumen resumen = new TipoAutoriaResumen(1L, "Nuevo", false);

        when(tipoAutoriaRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(tipoAutoriaMapper.toEntity(request)).thenReturn(entity);
        when(tipoAutoriaRepository.save(entity)).thenReturn(savedEntity);
        when(tipoAutoriaMapper.toResumen(savedEntity)).thenReturn(resumen);

        TipoAutoriaResumen result = tipoAutoriaService.crear(request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists")
    void crearDuplicateName() {
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("Existe");

        when(tipoAutoriaRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> tipoAutoriaService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un tipo de autoría con el nombre: Existe");
    }

    @Test
    @DisplayName("actualizar - Should update and return Resumen")
    void actualizarSuccess() {
        Long id = 1L;
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("Actualizado");
        TipoAutoria existingEntity = TipoAutoria.builder().id(id).nombre("Original").build();
        TipoAutoriaResumen resumen = new TipoAutoriaResumen(id, "Actualizado", false);

        when(tipoAutoriaRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(tipoAutoriaRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(tipoAutoriaRepository.save(existingEntity)).thenReturn(existingEntity);
        when(tipoAutoriaMapper.toResumen(existingEntity)).thenReturn(resumen);

        TipoAutoriaResumen result = tipoAutoriaService.actualizar(id, request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        Long id = 1L;
        TipoAutoriaRequest request = new TipoAutoriaRequest();

        when(tipoAutoriaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoAutoriaService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un tipo de autoría con ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when updating to existing name")
    void actualizarDuplicateName() {
        Long id = 1L;
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("Existe");
        TipoAutoria existingEntity = new TipoAutoria();
        existingEntity.setNombre("Original");

        when(tipoAutoriaRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(tipoAutoriaRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> tipoAutoriaService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro tipo de autoría con el nombre: Existe");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate when state is true")
    void cambiarEstadoActivar() {
        when(tipoAutoriaRepository.existsById(1L)).thenReturn(true);

        tipoAutoriaService.cambiarEstado(1L, true);

        verify(tipoAutoriaRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate when state is false")
    void cambiarEstadoDesactivar() {
        when(tipoAutoriaRepository.existsById(1L)).thenReturn(true);

        tipoAutoriaService.cambiarEstado(1L, false);

        verify(tipoAutoriaRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(tipoAutoriaRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> tipoAutoriaService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un tipo de autoría con ID: 1");
    }
}
