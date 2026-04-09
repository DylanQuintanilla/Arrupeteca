package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.ObraMapper;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.projection.ObraResumen;
import com.arrupeteca.persistence.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ObraServiceImplTest extends BaseServiceTest {
    @Mock private ObraRepository repository;
    @Mock private GeneroRepository generoRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private AutorRepository autorRepository;
    @Mock private TipoAutoriaRepository tipoAutoriaRepository;
    @Mock private ObraMapper mapper;
    @InjectMocks private ObraServiceImpl service;

    @Test
    @DisplayName("Should return paginated active obras")
    void shouldReturnPaginatedActive() {
        Page<ObraResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return obra by id when active")
    void shouldReturnByIdActivo() {
        ObraResumen resumen = mock(ObraResumen.class);
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorIdActivo(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should return all obras")
    void shouldReturnAll() {
        Page<ObraResumen> page = new PageImpl<>(List.of());
        when(repository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return search results")
    void shouldReturnSearch() {
        Page<ObraResumen> page = new PageImpl<>(List.of());
        when(repository.busquedaGlobal(anyString(), any(Pageable.class))).thenReturn(page);
        assertThat(service.busquedaGlobal("test", Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should create obra")
    void shouldCreate() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 1");
        request.setIdGeneros(new HashSet<>());
        request.setIdCategorias(new HashSet<>());
        request.setAutores(new HashSet<>());

        when(repository.existsByTituloIgnoreCase(anyString())).thenReturn(false);
        
        Obra entity = new Obra();
        entity.setId(1L);
        entity.setObraAutores(new HashSet<>());
        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        
        ObraResumen resumen = mock(ObraResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.crear(request)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should update obra")
    void shouldUpdate() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 2");
        request.setIdGeneros(new HashSet<>());
        request.setIdCategorias(new HashSet<>());
        request.setAutores(new HashSet<>());

        Obra existing = new Obra();
        existing.setId(1L);
        existing.setTitulo("Obra 1");
        existing.setObraAutores(new HashSet<>());
        
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByTituloIgnoreCase("Obra 2")).thenReturn(false);
        
        ObraResumen resumen = mock(ObraResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.actualizar(1L, request)).isEqualTo(resumen);
        verify(repository, atLeastOnce()).save(existing);
    }

    @Test
    @DisplayName("Should activate obra")
    void shouldActivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, true);
        verify(repository).activar(1L);
    }

    @Test
    @DisplayName("Should deactivate obra")
    void shouldDeactivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, false);
        verify(repository).desactivar(1L);
    }
}
