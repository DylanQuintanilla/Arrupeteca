package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.LibroMapper;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.repository.LibroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibroServiceImplTest {

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private LibroMapper libroMapper;

    @InjectMocks
    private LibroServiceImpl libroService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of Resumen")
    void obtenerTodosActivos() {
        Libro entity = Libro.builder().id(1L).isbn("123").build();
        Page<Libro> page = new PageImpl<>(List.of(entity));
        LibroResumen resumen = new LibroResumen(1L, "123", 2021, BigDecimal.TEN, "url", BigDecimal.ONE, false, "G", "C", "Obra", "Ed", "F");

        when(libroRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(libroMapper.toResumen(entity)).thenReturn(resumen);

        Page<LibroResumen> result = libroService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return DetalleResponse")
    void obtenerPorIdActivo() {
        Libro entity = Libro.builder().id(1L).isbn("123").build();
        LibroDetalleResponse detail = new LibroDetalleResponse(1L, "123", 2021, BigDecimal.TEN, "url", BigDecimal.ONE, false, "G", "C",
                "admin", null, "admin", null, null, null, null, Collections.emptySet());

        when(libroRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(libroMapper.toDetalleResponse(entity)).thenReturn(detail);

        LibroDetalleResponse result = libroService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(libroRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> libroService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Libro activo con ID: 1");
    }

    @Test
    @DisplayName("busquedaGlobal - Should return page of Resumen")
    void busquedaGlobal() {
        Libro entity = Libro.builder().id(1L).build();
        Page<Libro> page = new PageImpl<>(List.of(entity));
        LibroResumen resumen = mock(LibroResumen.class);
        when(libroRepository.busquedaGlobal("palabra", Pageable.unpaged())).thenReturn(page);
        when(libroMapper.toResumen(entity)).thenReturn(resumen);

        Page<LibroResumen> result = libroService.busquedaGlobal("palabra", Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("obtenerTodos - Should return all books")
    void obtenerTodos() {
        Libro entity = Libro.builder().id(1L).build();
        Page<Libro> page = new PageImpl<>(List.of(entity));
        LibroResumen resumen = mock(LibroResumen.class);
        when(libroRepository.findAllProjectedBy(Pageable.unpaged())).thenReturn(page);
        when(libroMapper.toResumen(entity)).thenReturn(resumen);

        Page<LibroResumen> result = libroService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("obtenerPorId - Should return DetalleResponse")
    void obtenerPorId() {
        Libro entity = Libro.builder().id(1L).build();
        LibroDetalleResponse detail = mock(LibroDetalleResponse.class);
        when(libroRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(libroMapper.toDetalleResponse(entity)).thenReturn(detail);

        LibroDetalleResponse result = libroService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(libroRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> libroService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Libro con ID: 1");
    }

    @Test
    @DisplayName("busquedaMaestraAdmin - Should handle String parameters")
    void busquedaMaestraAdminString() {
        Libro entity = Libro.builder().id(1L).build();
        Page<Libro> page = new PageImpl<>(List.of(entity));
        LibroResumen resumen = mock(LibroResumen.class);
        
        when(libroRepository.busquedaMaestraAdmin(eq(false), eq(1L), eq(1L), eq(1L), 
                eq(com.arrupeteca.persistence.enums.Grado.BACHI_GENERAL), 
                eq(com.arrupeteca.persistence.enums.Ciclo.PRIMER), 
                eq("term"), eq(2000), eq(2021), any(Pageable.class)))
                .thenReturn(page);
        when(libroMapper.toResumen(entity)).thenReturn(resumen);

        Page<LibroResumen> result = libroService.busquedaMaestraAdmin(
                false, 1L, 1L, 1L, "BACHI_GENERAL", "PRIMER", "term", 2000, 2021, Pageable.unpaged());
        
        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("busquedaMaestraAdmin - Should handle Enum parameters")
    void busquedaMaestraAdminEnum() {
        Libro entity = Libro.builder().id(1L).build();
        Page<Libro> page = new PageImpl<>(List.of(entity));
        LibroResumen resumen = mock(LibroResumen.class);
        
        when(libroRepository.busquedaMaestraAdmin(eq(false), eq(1L), eq(1L), eq(1L), 
                eq(com.arrupeteca.persistence.enums.Grado.BACHI_GENERAL), 
                eq(com.arrupeteca.persistence.enums.Ciclo.PRIMER), 
                eq("term"), eq(2000), eq(2021), any(Pageable.class)))
                .thenReturn(page);
        when(libroMapper.toResumen(entity)).thenReturn(resumen);

        Page<LibroResumen> result = libroService.busquedaMaestraAdmin(
                false, 1L, 1L, 1L, com.arrupeteca.persistence.enums.Grado.BACHI_GENERAL, 
                com.arrupeteca.persistence.enums.Ciclo.PRIMER, "term", 2000, 2021, Pageable.unpaged());
        
        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("busquedaMaestraAdmin - Should handle null/empty parameters")
    void busquedaMaestraAdminNull() {
        Libro entity = Libro.builder().id(1L).build();
        Page<Libro> page = new PageImpl<>(List.of(entity));
        LibroResumen resumen = mock(LibroResumen.class);
        
        when(libroRepository.busquedaMaestraAdmin(eq(false), eq(1L), eq(1L), eq(1L), 
                eq(null), eq(null), 
                eq("term"), eq(2000), eq(2021), any(Pageable.class)))
                .thenReturn(page);
        when(libroMapper.toResumen(entity)).thenReturn(resumen);

        Page<LibroResumen> result = libroService.busquedaMaestraAdmin(
                false, 1L, 1L, 1L, "", null, "term", 2000, 2021, Pageable.unpaged());
        
        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("buscarPorObraId - Should return results")
    void buscarPorObraId() {
        Libro entity = Libro.builder().id(1L).build();
        Page<Libro> page = new PageImpl<>(List.of(entity));
        LibroResumen resumen = mock(LibroResumen.class);
        when(libroRepository.buscarPorObraId(1L, Pageable.unpaged())).thenReturn(page);
        when(libroMapper.toResumen(entity)).thenReturn(resumen);

        Page<LibroResumen> result = libroService.buscarPorObraId(1L, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("crear - Should create and return DetalleResponse")
    void crearSuccess() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("9780596520687");
        Libro entity = new Libro();
        Libro saved = Libro.builder().id(1L).isbn("9780596520687").build();
        LibroDetalleResponse detail = mock(LibroDetalleResponse.class);

        when(libroRepository.existsByIsbn("9780596520687")).thenReturn(false);
        when(libroMapper.toEntity(request)).thenReturn(entity);
        when(libroRepository.save(entity)).thenReturn(saved);
        when(libroMapper.toDetalleResponse(saved)).thenReturn(detail);

        LibroDetalleResponse result = libroService.crear(request);

        assertThat(result).isEqualTo(detail);
        verify(libroRepository).save(entity);
    }

    @Test
    @DisplayName("crear - Should throw exception if ISBN exists")
    void crearIsbnExists() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("123");
        when(libroRepository.existsByIsbn("123")).thenReturn(true);

        assertThatThrownBy(() -> libroService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un libro con el ISBN: 123");
    }

    @Test
    @DisplayName("actualizar - Should update and return DetalleResponse")
    void actualizar() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("456");
        Libro existing = Libro.builder().id(1L).isbn("123").build();
        Libro saved = Libro.builder().id(1L).isbn("456").build();
        LibroDetalleResponse detail = mock(LibroDetalleResponse.class);

        when(libroRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(libroRepository.existsByIsbn("456")).thenReturn(false);
        when(libroRepository.save(existing)).thenReturn(saved);
        when(libroMapper.toDetalleResponse(saved)).thenReturn(detail);

        LibroDetalleResponse result = libroService.actualizar(1L, request);

        assertThat(result).isEqualTo(detail);
        verify(libroMapper).updateEntity(request, existing);
    }

    @Test
    @DisplayName("actualizar - Should update with same ISBN")
    void actualizarSameIsbn() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("123");
        Libro existing = Libro.builder().id(1L).isbn("123").build();
        LibroDetalleResponse detail = mock(LibroDetalleResponse.class);

        when(libroRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(libroRepository.save(existing)).thenReturn(existing);
        when(libroMapper.toDetalleResponse(existing)).thenReturn(detail);

        libroService.actualizar(1L, request);

        verify(libroRepository, never()).existsByIsbn("123");
    }

    @Test
    @DisplayName("actualizar - Should throw when not found")
    void actualizarNotFound() {
        when(libroRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> libroService.actualizar(1L, new LibroRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Libro con ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw when ISBN already exists in another book")
    void actualizarIsbnConflict() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("456");
        Libro existing = Libro.builder().id(1L).isbn("123").build();

        when(libroRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(libroRepository.existsByIsbn("456")).thenReturn(true);

        assertThatThrownBy(() -> libroService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro libro con el ISBN: 456");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate book")
    void activarLibro() {
        when(libroRepository.existsById(1L)).thenReturn(true);
        libroService.cambiarEstado(1L, true);
        verify(libroRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate book")
    void desactivarLibro() {
        when(libroRepository.existsById(1L)).thenReturn(true);
        libroService.cambiarEstado(1L, false);
        verify(libroRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw when not found")
    void cambiarEstadoNotFound() {
        when(libroRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> libroService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Libro con ID: 1");
    }
}
