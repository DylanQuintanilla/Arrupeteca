package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.detail.EdificioDetalleResponse;
import com.arrupeteca.dto.response.summary.EdificioResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EdificioMapper;
import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.repository.EdificioRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdificioServiceImplTest {

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
        EdificioResumen resumen = new EdificioResumen(1L, "Principal", false);

        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(mapper.toResumen(entity)).thenReturn(resumen);

        Page<EdificioResumen> result = service.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should return building by ID when active")
    void shouldReturnByIdActive() {
        Edificio entity = Edificio.builder().id(1L).nombre("Principal").build();
        EdificioDetalleResponse detail = new EdificioDetalleResponse(1L, "Principal", false, "admin", null, "admin", null);

        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDetalleResponse(entity)).thenReturn(detail);

        EdificioDetalleResponse result = service.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("Should return building by ID")
    void shouldReturnById() {
        Edificio entity = Edificio.builder().id(1L).nombre("Principal").build();
        EdificioDetalleResponse detail = new EdificioDetalleResponse(1L, "Principal", false, "admin", null, "admin", null);

        when(repository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDetalleResponse(entity)).thenReturn(detail);

        EdificioDetalleResponse result = service.obtenerPorId(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("Should throw exception when active building not found")
    void shouldThrowWhenActiveNotFound() {
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un edificio activo con ID: 1");
    }

    @Test
    @DisplayName("Should return all buildings for admin")
    void shouldReturnAllAdmin() {
        Edificio entity = Edificio.builder().id(1L).nombre("Principal").build();
        Page<Edificio> page = new PageImpl<>(List.of(entity));
        EdificioResumen resumen = new EdificioResumen(1L, "Principal", false);

        when(repository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        when(mapper.toResumen(entity)).thenReturn(resumen);

        Page<EdificioResumen> result = service.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when building not found")
    void shouldThrowWhenNotFound() {
        when(repository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un edificio con ID: 1");
    }

    @Test
    @DisplayName("Should create building")
    void shouldCreate() {
        EdificioRequest request = new EdificioRequest();
        request.setNombre("Nuevo");

        Edificio entity = Edificio.builder().id(1L).nombre("Nuevo").build();
        EdificioDetalleResponse detail = new EdificioDetalleResponse(1L, "Nuevo", false, "admin", null, "admin", null);

        when(repository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(any(Edificio.class))).thenReturn(entity);
        when(mapper.toDetalleResponse(entity)).thenReturn(detail);

        EdificioDetalleResponse result = service.crear(request);

        assertThat(result).isEqualTo(detail);
        verify(repository).save(any(Edificio.class));
    }

    @Test
    @DisplayName("Should throw exception when creating building with duplicate name")
    void shouldThrowOnCreateDuplicate() {
        EdificioRequest request = new EdificioRequest();
        request.setNombre("Existente");

        when(repository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un edificio con el nombre: Existente");
    }

    @Test
    @DisplayName("Should update building")
    void shouldUpdate() {
        EdificioRequest request = new EdificioRequest();
        request.setNombre("Actualizado");

        Edificio existing = Edificio.builder().id(1L).nombre("Principal").build();
        EdificioDetalleResponse detail = new EdificioDetalleResponse(1L, "Actualizado", false, "admin", null, "admin", null);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toDetalleResponse(existing)).thenReturn(detail);

        EdificioDetalleResponse result = service.actualizar(1L, request);

        assertThat(result).isEqualTo(detail);
        verify(mapper).updateEntity(request, existing);
    }

    @Test
    @DisplayName("Should update building with same name")
    void shouldUpdateSameName() {
        EdificioRequest request = new EdificioRequest();
        request.setNombre("Principal");

        Edificio existing = Edificio.builder().id(1L).nombre("Principal").build();
        EdificioDetalleResponse detail = new EdificioDetalleResponse(1L, "Principal", false, "admin", null, "admin", null);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toDetalleResponse(existing)).thenReturn(detail);

        EdificioDetalleResponse result = service.actualizar(1L, request);

        assertThat(result).isEqualTo(detail);
        verify(repository, never()).existsByNombreIgnoreCase(any());
    }

    @Test
    @DisplayName("Should throw when updating building to existing name")
    void shouldThrowOnUpdateDuplicate() {
        EdificioRequest request = new EdificioRequest();
        request.setNombre("Ocupado");

        Edificio existing = Edificio.builder().id(1L).nombre("Principal").build();

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByNombreIgnoreCase("Ocupado")).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro edificio con el nombre: Ocupado");
    }

    @Test
    @DisplayName("Should throw when updating non-existent building")
    void shouldThrowOnUpdateNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(1L, new EdificioRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un edificio con ID: 1");
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
                .hasMessageContaining("No se encontró un edificio con ID: 1");
        verify(repository, never()).activar(any());
        verify(repository, never()).desactivar(any());
    }
}
