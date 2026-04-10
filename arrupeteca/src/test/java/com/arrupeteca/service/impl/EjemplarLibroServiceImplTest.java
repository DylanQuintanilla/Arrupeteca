package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EjemplarLibroMapper;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

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
    @DisplayName("Should throw exception when active ejemplar not found")
    void shouldThrowWhenActiveNotFound() {
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Ejemplar activo con ID: 1");
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
    @DisplayName("Should return ejemplar by id")
    void shouldReturnById() {
        EjemplarLibroResumen resumen = mock(EjemplarLibroResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorId(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when ejemplar not found by id")
    void shouldThrowWhenNotFoundById() {
        when(repository.findProjectedById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Ejemplar con ID: 1");
    }

    @Test
    @DisplayName("Should return busqueda maestra admin results")
    void shouldReturnBusquedaMaestraAdmin() {
        Page<EjemplarLibroResumen> page = new PageImpl<>(List.of());
        when(repository.busquedaMaestraAdmin(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        assertThat(service.busquedaMaestraAdmin(false, 1L, 1L, "BUEN_ESTADO", "DISPONIBLE", "term", Pageable.unpaged()))
                .isEqualTo(page);
        
        verify(repository).busquedaMaestraAdmin(eq(false), eq(1L), eq(1L), eq(EstadoFisico.BUEN_ESTADO), eq(Disponibilidad.DISPONIBLE), eq("term"), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return busqueda maestra admin results with null params")
    void shouldReturnBusquedaMaestraAdminWithNulls() {
        Page<EjemplarLibroResumen> page = new PageImpl<>(List.of());
        when(repository.busquedaMaestraAdmin(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        assertThat(service.busquedaMaestraAdmin(null, null, null, null, null, null, Pageable.unpaged()))
                .isEqualTo(page);
        
        verify(repository).busquedaMaestraAdmin(eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), any(Pageable.class));
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
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Should throw exception when libro not found on create")
    void shouldThrowWhenLibroNotFoundOnCreate() {
        EjemplarLibroRequest request = new EjemplarLibroRequest();
        request.setIdLibro(1L);
        when(libroRepository.findById(1L)).thenReturn(Optional.empty());
        when(mapper.toEntity(any())).thenReturn(new EjemplarLibro());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El Libro indicado no existe");
    }

    @Test
    @DisplayName("Should throw exception when mueble not found on create")
    void shouldThrowWhenMuebleNotFoundOnCreate() {
        EjemplarLibroRequest request = new EjemplarLibroRequest();
        request.setIdMueble(1L);
        when(muebleRepository.findById(1L)).thenReturn(Optional.empty());
        when(mapper.toEntity(any())).thenReturn(new EjemplarLibro());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El Mueble indicado no existe");
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
    @DisplayName("Should throw exception when ejemplar not found on update")
    void shouldThrowWhenNotFoundOnUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(1L, new EjemplarLibroRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Ejemplar con ID: 1");
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

    @Test
    @DisplayName("Should throw exception when ejemplar not found on status change")
    void shouldThrowWhenNotFoundOnStatusChange() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> service.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Ejemplar con ID: 1");
        verify(repository, never()).activar(any());
        verify(repository, never()).desactivar(any());
    }
}

