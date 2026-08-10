package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.TipoAutoriaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.TipoAutoriaMapper;
import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.repository.TipoAutoriaRepository;
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

class TipoAutoriaServiceImplTest extends BaseServiceTest {

    @Mock
    private TipoAutoriaRepository tipoAutoriaRepository;

    @Mock
    private TipoAutoriaMapper tipoAutoriaMapper;

    @InjectMocks
    private TipoAutoriaServiceImpl tipoAutoriaService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of TipoAutoriaResponse")
    void obtenerTodosActivos() {
        TipoAutoria entity = TipoAutoria.builder().id(1L).nombre("Test").build();
        Page<TipoAutoria> page = new PageImpl<>(List.of(entity));
        TipoAutoriaResponse response = new TipoAutoriaResponse(1L, "Test", false);

        when(tipoAutoriaRepository.buscarActivosPorNombre(any())).thenReturn(List.of(entity));
        when(tipoAutoriaMapper.toResponse(entity)).thenReturn(response);

        List<TipoAutoriaResponse> result = tipoAutoriaService.obtenerTodosActivos(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(response);
    }


    @Test
    @DisplayName("obtenerTodos - Should return page of TipoAutoriaResponse")
    void obtenerTodos() {
        TipoAutoria entity = TipoAutoria.builder().id(1L).nombre("Test").build();
        Page<TipoAutoria> page = new PageImpl<>(List.of(entity));
        TipoAutoriaResponse response = new TipoAutoriaResponse(1L, "Test", false);

        when(tipoAutoriaRepository.busquedaAdmin(any(), any(), any())).thenReturn(page);
        when(tipoAutoriaMapper.toResponse(entity)).thenReturn(response);

        Page<TipoAutoriaResponse> result = tipoAutoriaService.obtenerTodos(null, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should return TipoAutoriaResponse when found")
    void obtenerPorIdSuccess() {
        TipoAutoria entity = TipoAutoria.builder().id(1L).nombre("Test").build();
        TipoAutoriaResponse response = new TipoAutoriaResponse(1L, "Test", false);
        
        when(tipoAutoriaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(tipoAutoriaMapper.toResponse(entity)).thenReturn(response);

        TipoAutoriaResponse result = tipoAutoriaService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(tipoAutoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoAutoriaService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("crear - Should create and return TipoAutoriaResponse")
    void crearSuccess() {
        TipoAutoriaRequest request = TipoAutoriaRequest.builder().nombre("Nuevo").build();
        TipoAutoria entity = TipoAutoria.builder().id(1L).nombre("Nuevo").build();
        TipoAutoria savedEntity = TipoAutoria.builder().id(1L).nombre("Nuevo").build();
        TipoAutoriaResponse response = new TipoAutoriaResponse(1L, "Nuevo", false);

        when(tipoAutoriaRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(tipoAutoriaMapper.toEntity(request)).thenReturn(entity);
        when(tipoAutoriaRepository.save(entity)).thenReturn(savedEntity);
        when(tipoAutoriaMapper.toResponse(savedEntity)).thenReturn(response);

        TipoAutoriaResponse result = tipoAutoriaService.crear(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists")
    void crearDuplicateName() {
        TipoAutoriaRequest request = TipoAutoriaRequest.builder().nombre("Existe").build();

        when(tipoAutoriaRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> tipoAutoriaService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con el nombre: Existe");
    }

    @Test
    @DisplayName("actualizar - Should update and return TipoAutoriaResponse")
    void actualizarSuccess() {
        Long id = 1L;
        TipoAutoriaRequest request = TipoAutoriaRequest.builder().nombre("Actualizado").build();
        TipoAutoria existingEntity = TipoAutoria.builder().id(1L).nombre("Original").build();
        TipoAutoriaResponse response = new TipoAutoriaResponse(id, "Actualizado", false);

        when(tipoAutoriaRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(tipoAutoriaRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(tipoAutoriaRepository.save(existingEntity)).thenReturn(existingEntity);
        when(tipoAutoriaMapper.toResponse(existingEntity)).thenReturn(response);

        TipoAutoriaResponse result = tipoAutoriaService.actualizar(id, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        Long id = 1L;
        TipoAutoriaRequest request = TipoAutoriaRequest.builder().nombre("Cualquiera").build();

        when(tipoAutoriaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoAutoriaService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when updating to existing name")
    void actualizarDuplicateName() {
        Long id = 1L;
        TipoAutoriaRequest request = TipoAutoriaRequest.builder().nombre("Existe").build();
        TipoAutoria existingEntity = TipoAutoria.builder().id(1L).nombre("Original").build();

        when(tipoAutoriaRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(tipoAutoriaRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> tipoAutoriaService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con el nombre: Existe");
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
                .hasMessageContaining("con ID: 1");
    }
}
