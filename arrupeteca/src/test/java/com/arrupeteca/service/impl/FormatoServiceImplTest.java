package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.FormatoResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.FormatoMapper;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.repository.FormatoRepository;
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

class FormatoServiceImplTest extends BaseServiceTest {

    @Mock
    private FormatoRepository formatoRepository;

    @Mock
    private FormatoMapper formatoMapper;

    @InjectMocks
    private FormatoServiceImpl formatoService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of FormatoResponse")
    void obtenerTodosActivos() {
        Formato entity = Formato.builder().id(1L).nombre("Test").build();
        Page<Formato> page = new PageImpl<>(List.of(entity));
        FormatoResponse response = new FormatoResponse(1L, "Test", false);

        when(formatoRepository.buscarActivosPorNombre(any())).thenReturn(List.of(entity));
        when(formatoMapper.toResponse(entity)).thenReturn(response);

        List<FormatoResponse> result = formatoService.obtenerTodosActivos(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(response);
    }



    @Test
    @DisplayName("obtenerTodos - Should return page of FormatoResponse")
    void obtenerTodos() {
        Formato entity = Formato.builder().id(1L).nombre("Test").build();
        Page<Formato> page = new PageImpl<>(List.of(entity));
        FormatoResponse response = new FormatoResponse(1L, "Test", false);
        
        when(formatoRepository.busquedaAdmin(any(), any(), any())).thenReturn(page);
        when(formatoMapper.toResponse(entity)).thenReturn(response);

        Page<FormatoResponse> result = formatoService.obtenerTodos(null, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should return FormatoResponse when found")
    void obtenerPorIdSuccess() {
        Formato entity = Formato.builder().id(1L).nombre("Test").build();
        FormatoResponse response = new FormatoResponse(1L, "Test", false);
        
        when(formatoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(formatoMapper.toResponse(entity)).thenReturn(response);

        FormatoResponse result = formatoService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(formatoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formatoService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("crear - Should create and return FormatoResponse")
    void crearSuccess() {
        FormatoRequest request = FormatoRequest.builder().nombre("Nuevo").build();
        Formato entity = Formato.builder().id(1L).nombre("Nuevo").build();
        Formato savedEntity = Formato.builder().id(1L).nombre("Nuevo").build();
        FormatoResponse response = new FormatoResponse(1L, "Nuevo", false);

        when(formatoRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(formatoMapper.toEntity(request)).thenReturn(entity);
        when(formatoRepository.save(entity)).thenReturn(savedEntity);
        when(formatoMapper.toResponse(savedEntity)).thenReturn(response);

        FormatoResponse result = formatoService.crear(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists")
    void crearDuplicateName() {
        FormatoRequest request = FormatoRequest.builder().nombre("Existe").build();

        when(formatoRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> formatoService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con el nombre: Existe");
    }

    @Test
    @DisplayName("actualizar - Should update and return FormatoResponse")
    void actualizarSuccess() {
        Long id = 1L;
        FormatoRequest request = FormatoRequest.builder().nombre("Actualizado").build();
        Formato existingEntity = Formato.builder().id(1L).nombre("Original").build();
        FormatoResponse response = new FormatoResponse(id, "Actualizado", false);

        when(formatoRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(formatoRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(formatoRepository.save(existingEntity)).thenReturn(existingEntity);
        when(formatoMapper.toResponse(existingEntity)).thenReturn(response);

        FormatoResponse result = formatoService.actualizar(id, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        Long id = 1L;
        FormatoRequest request = FormatoRequest.builder().nombre("Cualquiera").build();

        when(formatoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formatoService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when updating to existing name")
    void actualizarDuplicateName() {
        Long id = 1L;
        FormatoRequest request = FormatoRequest.builder().nombre("Existe").build();
        Formato existingEntity = Formato.builder().id(1L).nombre("Original").build();

        when(formatoRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(formatoRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> formatoService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con el nombre: Existe");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate when state is true")
    void cambiarEstadoActivar() {
        when(formatoRepository.existsById(1L)).thenReturn(true);

        formatoService.cambiarEstado(1L, true);

        verify(formatoRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate when state is false")
    void cambiarEstadoDesactivar() {
        when(formatoRepository.existsById(1L)).thenReturn(true);

        formatoService.cambiarEstado(1L, false);

        verify(formatoRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(formatoRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> formatoService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }
}
