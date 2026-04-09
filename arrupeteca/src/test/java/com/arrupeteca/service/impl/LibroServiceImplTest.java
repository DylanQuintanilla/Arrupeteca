package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.LibroMapper;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.projection.LibroResumen;
import com.arrupeteca.persistence.repository.*;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LibroServiceImplTest extends BaseServiceTest {
    @Mock private LibroRepository repository;
    @Mock private LibroMapper mapper;
    @Mock private ObraRepository obraRepository;
    @Mock private EditorialRepository editorialRepository;
    @Mock private FormatoRepository formatoRepository;
    @Mock private IdiomaRepository idiomaRepository;
    @InjectMocks private LibroServiceImpl service;

    @Test
    @DisplayName("Should return paginated active libros")
    void shouldReturnPaginatedActive() {
        Page<LibroResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return libro by id when active")
    void shouldReturnByIdActivo() {
        LibroResumen resumen = mock(LibroResumen.class);
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorIdActivo(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should return all libros")
    void shouldReturnAll() {
        Page<LibroResumen> page = new PageImpl<>(List.of());
        when(repository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return search results")
    void shouldReturnSearch() {
        Page<LibroResumen> page = new PageImpl<>(List.of());
        when(repository.busquedaGlobal(anyString(), any(Pageable.class))).thenReturn(page);
        assertThat(service.busquedaGlobal("test", Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return libros by obra")
    void shouldReturnByObra() {
        when(obraRepository.existsById(1L)).thenReturn(true);
        Page<LibroResumen> page = new PageImpl<>(List.of());
        when(repository.buscarPorObraId(eq(1L), any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerPorObra(1L, Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should create libro")
    void shouldCreate() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("1234567890");

        when(repository.existsByIsbn(anyString())).thenReturn(false);
        
        Libro entity = new Libro();
        entity.setId(1L);
        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        
        LibroResumen resumen = mock(LibroResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.crear(request)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should update libro")
    void shouldUpdate() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("0987654321");

        Libro existing = new Libro();
        existing.setId(1L);
        existing.setIsbn("1234567890");
        
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByIsbn("0987654321")).thenReturn(false);
        
        LibroResumen resumen = mock(LibroResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.actualizar(1L, request)).isEqualTo(resumen);
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Should activate libro")
    void shouldActivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, true);
        verify(repository).activar(1L);
    }

    @Test
    @DisplayName("Should deactivate libro")
    void shouldDeactivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, false);
        verify(repository).desactivar(1L);
    }
}
