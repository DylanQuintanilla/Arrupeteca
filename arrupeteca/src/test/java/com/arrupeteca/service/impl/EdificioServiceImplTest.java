package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EdificioMapper;
import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.projection.EdificioResumen;
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
import static org.mockito.Mockito.*;

class EdificioServiceImplTest extends BaseServiceTest {
    @Mock private EdificioRepository repository;
    @Mock private EdificioMapper mapper;
    @InjectMocks private EdificioServiceImpl service;

    @Test
    @DisplayName("Should return paginated active buildings")
    void shouldReturnPaginatedActive() {
        Page<EdificioResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return building by id when active")
    void shouldReturnByIdActivo() {
        EdificioResumen resumen = mock(EdificioResumen.class);
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorIdActivo(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should return all buildings")
    void shouldReturnAll() {
        Page<EdificioResumen> page = new PageImpl<>(List.of());
        when(repository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return building by id")
    void shouldReturnById() {
        EdificioResumen resumen = mock(EdificioResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorId(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should create building")
    void shouldCreate() {
        EdificioRequest request = new EdificioRequest();
        request.setNombre("Edificio A");

        when(repository.existsByNombreIgnoreCase(anyString())).thenReturn(false);
        
        Edificio entity = new Edificio();
        entity.setId(1L);
        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        
        EdificioResumen resumen = mock(EdificioResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.crear(request)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should update building")
    void shouldUpdate() {
        EdificioRequest request = new EdificioRequest();
        request.setNombre("Edificio B");

        Edificio existing = new Edificio();
        existing.setId(1L);
        existing.setNombre("Edificio A");
        
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByNombreIgnoreCase("Edificio B")).thenReturn(false);
        
        EdificioResumen resumen = mock(EdificioResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.actualizar(1L, request)).isEqualTo(resumen);
        verify(repository).save(existing);
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
}
