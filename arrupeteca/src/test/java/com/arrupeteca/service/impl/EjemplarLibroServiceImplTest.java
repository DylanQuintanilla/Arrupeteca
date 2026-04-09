package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EjemplarLibroMapper;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.projection.EjemplarLibroResumen;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.persistence.repository.MuebleRepository;
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

class EjemplarLibroServiceImplTest extends BaseServiceTest {
    @Mock private EjemplarLibroRepository repository;
    @Mock private EjemplarLibroMapper mapper;
    @Mock private LibroRepository libroRepository;
    @Mock private MuebleRepository muebleRepository;
    @InjectMocks private EjemplarLibroServiceImpl service;

    @Test
    @DisplayName("Should return paginated active ejemplares")
    void shouldReturnPaginatedActive() {
        Page<EjemplarLibroResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return ejemplar by id when active")
    void shouldReturnByIdActivo() {
        EjemplarLibroResumen resumen = mock(EjemplarLibroResumen.class);
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorIdActivo(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should return all ejemplares")
    void shouldReturnAll() {
        Page<EjemplarLibroResumen> page = new PageImpl<>(List.of());
        when(repository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return search results")
    void shouldReturnSearch() {
        Page<EjemplarLibroResumen> page = new PageImpl<>(List.of());
        when(repository.busquedaGlobal(anyString(), any(Pageable.class))).thenReturn(page);
        assertThat(service.busquedaGlobal("test", Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should create ejemplar")
    void shouldCreate() {
        EjemplarLibroRequest request = new EjemplarLibroRequest();
        request.setIdLibro(1L);
        request.setIdMueble(1L);

        when(libroRepository.findById(1L)).thenReturn(Optional.of(new Libro()));
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(new Mueble()));
        
        EjemplarLibro entity = new EjemplarLibro();
        entity.setId(1L);
        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        
        EjemplarLibroResumen resumen = mock(EjemplarLibroResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.crear(request)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should update ejemplar")
    void shouldUpdate() {
        EjemplarLibroRequest request = new EjemplarLibroRequest();
        request.setIdLibro(1L);
        request.setIdMueble(1L);

        EjemplarLibro existing = new EjemplarLibro();
        existing.setId(1L);
        
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(libroRepository.findById(1L)).thenReturn(Optional.of(new Libro()));
        when(muebleRepository.findById(1L)).thenReturn(Optional.of(new Mueble()));
        
        EjemplarLibroResumen resumen = mock(EjemplarLibroResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.actualizar(1L, request)).isEqualTo(resumen);
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Should activate ejemplar")
    void shouldActivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, true);
        verify(repository).activar(1L);
    }

    @Test
    @DisplayName("Should deactivate ejemplar")
    void shouldDeactivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, false);
        verify(repository).desactivar(1L);
    }
}
