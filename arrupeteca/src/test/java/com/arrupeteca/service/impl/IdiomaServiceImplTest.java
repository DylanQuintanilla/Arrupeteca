package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.IdiomaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.IdiomaMapper;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.repository.IdiomaRepository;
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

class IdiomaServiceImplTest extends BaseServiceTest {

    @Mock
    private IdiomaRepository idiomaRepository;

    @Mock
    private IdiomaMapper idiomaMapper;

    @InjectMocks
    private IdiomaServiceImpl idiomaService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of IdiomaResponse")
    void obtenerTodosActivos() {
        Idioma entity = Idioma.builder().id(1L).nombre("Test").build();
        Page<Idioma> page = new PageImpl<>(List.of(entity));
        IdiomaResponse response = new IdiomaResponse(1L, "Test", false);

        when(idiomaRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(idiomaMapper.toResponse(entity)).thenReturn(response);

        Page<IdiomaResponse> result = idiomaService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return IdiomaResponse when found and active")
    void obtenerPorIdActivoSuccess() {
        IdiomaResponse response = new IdiomaResponse(1L, "Test", false);
        Idioma entity = Idioma.builder().id(1L).nombre("Test").build();
        
        when(idiomaRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(idiomaMapper.toResponse(entity)).thenReturn(response);

        IdiomaResponse result = idiomaService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(idiomaRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> idiomaService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un idioma activo con ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return page of IdiomaResponse")
    void obtenerTodos() {
        Idioma entity = Idioma.builder().id(1L).nombre("Test").build();
        Page<Idioma> page = new PageImpl<>(List.of(entity));
        IdiomaResponse response = new IdiomaResponse(1L, "Test", false);
        
        when(idiomaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(idiomaMapper.toResponse(entity)).thenReturn(response);

        Page<IdiomaResponse> result = idiomaService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should return IdiomaResponse when found")
    void obtenerPorIdSuccess() {
        Idioma entity = Idioma.builder().id(1L).nombre("Test").build();
        IdiomaResponse response = new IdiomaResponse(1L, "Test", false);
        
        when(idiomaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(idiomaMapper.toResponse(entity)).thenReturn(response);

        IdiomaResponse result = idiomaService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(idiomaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> idiomaService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un idioma con ID: 1");
    }

    @Test
    @DisplayName("crear - Should create and return IdiomaResponse")
    void crearSuccess() {
        IdiomaRequest request = IdiomaRequest.builder().nombre("Nuevo").build();
        Idioma entity = Idioma.builder().id(1L).nombre("Nuevo").build();
        Idioma savedEntity = Idioma.builder().id(1L).nombre("Nuevo").build();
        IdiomaResponse response = new IdiomaResponse(1L, "Nuevo", false);

        when(idiomaRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(idiomaMapper.toEntity(request)).thenReturn(entity);
        when(idiomaRepository.save(entity)).thenReturn(savedEntity);
        when(idiomaMapper.toResponse(savedEntity)).thenReturn(response);

        IdiomaResponse result = idiomaService.crear(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists")
    void crearDuplicateName() {
        IdiomaRequest request = IdiomaRequest.builder().nombre("Existe").build();

        when(idiomaRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> idiomaService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un idioma con el nombre: Existe");
    }

    @Test
    @DisplayName("actualizar - Should update and return IdiomaResponse")
    void actualizarSuccess() {
        Long id = 1L;
        IdiomaRequest request = IdiomaRequest.builder().nombre("Actualizado").build();
        Idioma existingEntity = Idioma.builder().id(1L).nombre("Original").build();
        IdiomaResponse response = new IdiomaResponse(id, "Actualizado", false);

        when(idiomaRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(idiomaRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(idiomaRepository.save(existingEntity)).thenReturn(existingEntity);
        when(idiomaMapper.toResponse(existingEntity)).thenReturn(response);

        IdiomaResponse result = idiomaService.actualizar(id, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        Long id = 1L;
        IdiomaRequest request = IdiomaRequest.builder().nombre("Cualquiera").build();

        when(idiomaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> idiomaService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un idioma con ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when updating to existing name")
    void actualizarDuplicateName() {
        Long id = 1L;
        IdiomaRequest request = IdiomaRequest.builder().nombre("Existe").build();
        Idioma existingEntity = Idioma.builder().id(1L).nombre("Original").build();

        when(idiomaRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(idiomaRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> idiomaService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro idioma con el nombre: Existe");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate when state is true")
    void cambiarEstadoActivar() {
        when(idiomaRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        idiomaService.cambiarEstado(1L, true);

        verify(idiomaRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate when state is false")
    void cambiarEstadoDesactivar() {
        when(idiomaRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        idiomaService.cambiarEstado(1L, false);

        verify(idiomaRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(idiomaRepository.countByIdIncludeDeleted(1L)).thenReturn(0L);

        assertThatThrownBy(() -> idiomaService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un idioma con ID: 1");
    }
}
