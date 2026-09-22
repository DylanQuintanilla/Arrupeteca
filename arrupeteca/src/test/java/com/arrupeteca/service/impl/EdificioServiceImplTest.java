package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EdificioMapper;
import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.repository.EdificioRepository;
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

class EdificioServiceImplTest extends BaseServiceTest {

    @Mock
    private EdificioRepository repository;

    @Mock
    private EdificioMapper mapper;

    @InjectMocks
    private EdificioServiceImpl service;

    @Test
    @DisplayName("Should return active buildings")
    void shouldReturnActive() {
        Edificio entity = Edificio.builder().id(1L).nombre("Principal").build();
        Page<Edificio> page = new PageImpl<>(List.of(entity));
        EdificioResponse response = new EdificioResponse(1L, "Principal", false);

        when(repository.buscarActivosPorNombre(any())).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<EdificioResponse> result = service.obtenerTodosActivos(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("Should return building by ID when active")
    void shouldReturnByIdActive() {
        Edificio entity = Edificio.builder().id(1L).nombre("Principal").build();
        EdificioResponse response = new EdificioResponse(1L, "Principal", false);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        EdificioResponse result = service.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should return building by ID")
    void shouldReturnById() {
        Edificio entity = Edificio.builder().id(1L).nombre("Principal").build();
        EdificioResponse response = new EdificioResponse(1L, "Principal", false);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        EdificioResponse result = service.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when active building not found")
    void shouldThrowWhenActiveNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("Should return all buildings for admin")
    void shouldReturnAllAdmin() {
        Edificio entity = Edificio.builder().id(1L).nombre("Principal").build();
        Page<Edificio> page = new PageImpl<>(List.of(entity));
        EdificioResponse response = new EdificioResponse(1L, "Principal", false);

        when(repository.busquedaAdmin(any(), any(), any())).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(response);

        Page<EdificioResponse> result = service.obtenerTodos(null, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when building not found")
    void shouldThrowWhenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("Should create building")
    void shouldCreate() {
        EdificioRequest request = EdificioRequest.builder().nombre("Nuevo").build();
        Edificio entity = Edificio.builder().id(1L).nombre("Nuevo").build();
        EdificioResponse response = new EdificioResponse(1L, "Nuevo", false);

        when(repository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(any(Edificio.class))).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        EdificioResponse result = service.crear(request);

        assertThat(result).isEqualTo(response);
        verify(repository).save(any(Edificio.class));
    }

    @Test
    @DisplayName("Should throw exception when creating building with duplicate name")
    void shouldThrowOnCreateDuplicate() {
        EdificioRequest request = EdificioRequest.builder().nombre("Existente").build();

        when(repository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con el nombre: Existente");
    }

    @Test
    @DisplayName("Should update building")
    void shouldUpdate() {
        EdificioRequest request = EdificioRequest.builder().nombre("Actualizado").build();
        Edificio existing = Edificio.builder().id(1L).nombre("Principal").build();
        EdificioResponse response = new EdificioResponse(1L, "Actualizado", false);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toResponse(existing)).thenReturn(response);

        EdificioResponse result = service.actualizar(1L, request);

        assertThat(result).isEqualTo(response);
        verify(mapper).updateEntity(request, existing);
    }

    @Test
    @DisplayName("Should update building with same name")
    void shouldUpdateSameName() {
        EdificioRequest request = EdificioRequest.builder().nombre("Principal").build();
        Edificio existing = Edificio.builder().id(1L).nombre("Principal").build();
        EdificioResponse response = new EdificioResponse(1L, "Principal", false);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toResponse(existing)).thenReturn(response);

        EdificioResponse result = service.actualizar(1L, request);

        assertThat(result).isEqualTo(response);
        verify(repository, never()).existsByNombreIgnoreCase(any());
    }

    @Test
    @DisplayName("Should throw when updating building to existing name")
    void shouldThrowOnUpdateDuplicate() {
        EdificioRequest request = EdificioRequest.builder().nombre("Ocupado").build();
        Edificio existing = Edificio.builder().id(1L).nombre("Principal").build();

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByNombreIgnoreCase("Ocupado")).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con el nombre: Ocupado");
    }

    @Test
    @DisplayName("Should throw when updating non-existent building")
    void shouldThrowOnUpdateNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(1L, EdificioRequest.builder().nombre("Cualquiera").build()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("Should activate building")
    void shouldActivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, true);
        verify(repository).activar(1L);
    }

    @Test
    @DisplayName("Should deactivate building")
    void shouldDeactivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, false);
        verify(repository).desactivar(1L);
    }

    @Test
    @DisplayName("Should throw exception when building not found on status change")
    void shouldThrowWhenNotFoundOnStatusChange() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> service.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
        verify(repository, never()).activar(any());
        verify(repository, never()).desactivar(any());
    }
}
