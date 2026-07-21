package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.GeneroResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.repository.GeneroRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GeneroServiceImplTest extends BaseServiceTest {

    @Mock
    private GeneroRepository generoRepository;

    @Mock
    private GeneroMapper generoMapper;

    @InjectMocks
    private GeneroServiceImpl generoService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of GeneroResponse")
    void obtenerTodosActivos() {
        Genero entity = Genero.builder().id(1L).nombre("Test").build();
        Page<Genero> page = new PageImpl<>(List.of(entity));
        GeneroResponse response = new GeneroResponse(1L, "Test", false);

        when(generoRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(generoMapper.toResponse(entity)).thenReturn(response);

        Page<GeneroResponse> result = generoService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return GeneroResponse")
    void obtenerPorIdActivo() {
        Genero entity = Genero.builder().id(1L).nombre("Test").build();
        GeneroResponse response = new GeneroResponse(1L, "Test", false);

        when(generoRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(generoMapper.toResponse(entity)).thenReturn(response);

        GeneroResponse result = generoService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(generoRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generoService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un género activo con ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return all genres")
    void obtenerTodos() {
        Genero entity = Genero.builder().id(1L).nombre("Test").build();
        Page<Genero> page = new PageImpl<>(List.of(entity));
        GeneroResponse response = new GeneroResponse(1L, "Test", false);

        when(generoRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(generoMapper.toResponse(entity)).thenReturn(response);

        Page<GeneroResponse> result = generoService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId - Should return GeneroResponse")
    void obtenerPorId() {
        Genero entity = Genero.builder().id(1L).nombre("Test").build();
        GeneroResponse response = new GeneroResponse(1L, "Test", false);

        when(generoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(generoMapper.toResponse(entity)).thenReturn(response);

        GeneroResponse result = generoService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(generoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generoService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un género con ID: 1");
    }

    @Test
    @DisplayName("crear - Should save and return GeneroResponse")
    void crear() {
        GeneroRequest request = GeneroRequest.builder().nombre("Nuevo").build();
        Genero entity = Genero.builder().id(1L).nombre("Nuevo").build();
        GeneroResponse response = new GeneroResponse(1L, "Nuevo", false);

        when(generoRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(generoMapper.toEntity(request)).thenReturn(entity);
        when(generoRepository.save(any(Genero.class))).thenReturn(entity);
        when(generoMapper.toResponse(entity)).thenReturn(response);

        GeneroResponse result = generoService.crear(request);

        assertThat(result).isEqualTo(response);
        verify(generoRepository).save(any(Genero.class));
    }

    @Test
    @DisplayName("crear - Should throw when name exists")
    void crearDuplicate() {
        GeneroRequest request = GeneroRequest.builder().nombre("Existe").build();
        when(generoRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> generoService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un género con el nombre: Existe");
    }

    @Test
    @DisplayName("actualizar - Should update and return GeneroResponse")
    void actualizar() {
        GeneroRequest request = GeneroRequest.builder().nombre("Actualizado").build();
        Genero existing = Genero.builder().id(1L).nombre("Test").build();
        GeneroResponse response = new GeneroResponse(1L, "Actualizado", false);

        when(generoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(generoRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(generoRepository.save(existing)).thenReturn(existing);
        when(generoMapper.toResponse(existing)).thenReturn(response);

        GeneroResponse result = generoService.actualizar(1L, request);

        assertThat(result).isEqualTo(response);
        verify(generoMapper).updateEntity(request, existing);
    }

    @Test
    @DisplayName("actualizar - Should update with same name")
    void actualizarSameName() {
        GeneroRequest request = GeneroRequest.builder().nombre("Test").build();
        Genero existing = Genero.builder().id(1L).nombre("Test").build();
        GeneroResponse response = new GeneroResponse(1L, "Test", false);

        when(generoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(generoRepository.save(existing)).thenReturn(existing);
        when(generoMapper.toResponse(existing)).thenReturn(response);

        generoService.actualizar(1L, request);

        verify(generoRepository, never()).existsByNombreIgnoreCase(any());
    }

    @Test
    @DisplayName("actualizar - Should throw when new name duplicate")
    void actualizarDuplicate() {
        GeneroRequest request = GeneroRequest.builder().nombre("Ocupado").build();
        Genero existing = Genero.builder().id(1L).nombre("Test").build();

        when(generoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(generoRepository.existsByNombreIgnoreCase("Ocupado")).thenReturn(true);

        assertThatThrownBy(() -> generoService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro género con el nombre: Ocupado");
    }

    @Test
    @DisplayName("actualizar - Should throw when not found")
    void actualizarNotFound() {
        when(generoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generoService.actualizar(1L, GeneroRequest.builder().nombre("Cualquiera").build()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un género con ID: 1");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate")
    void activate() {
        when(generoRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);
        generoService.cambiarEstado(1L, true);
        verify(generoRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate")
    void deactivate() {
        when(generoRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);
        generoService.cambiarEstado(1L, false);
        verify(generoRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw when not found")
    void statusNotFound() {
        when(generoRepository.countByIdIncludeDeleted(1L)).thenReturn(0L);
        assertThatThrownBy(() -> generoService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un género con ID: 1");
    }
}
