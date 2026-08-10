package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.LibroMapper;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.persistence.repository.ObraRepository;
import com.arrupeteca.persistence.repository.EditorialRepository;
import com.arrupeteca.persistence.repository.FormatoRepository;
import com.arrupeteca.persistence.repository.IdiomaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LibroServiceImplTest extends BaseServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private ObraRepository obraRepository;

    @Mock
    private EditorialRepository editorialRepository;

    @Mock
    private FormatoRepository formatoRepository;

    @Mock
    private IdiomaRepository idiomaRepository;

    @Mock
    private LibroMapper libroMapper;

    @InjectMocks
    private LibroServiceImpl libroService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of Resumen")
    void obtenerTodosActivos() {
        LibroResumen resumen = mock(LibroResumen.class);
        Page<LibroResumen> page = new PageImpl<>(List.of(resumen));

        when(libroRepository.busquedaMaestraAdminProyectada(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        Page<LibroResumen> result = libroService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return DetalleResponse")
    void obtenerPorIdActivo() {
        Libro entity = Libro.builder().id(1L).build();
        LibroDetalleResponse detail = mock(LibroDetalleResponse.class);

        when(libroRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(libroMapper.toDetalleResponse(entity)).thenReturn(detail);

        LibroDetalleResponse result = libroService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(libroRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> libroService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("busquedaGlobal - Should return page of Resumen")
    void busquedaGlobal() {
        LibroResumen resumen = mock(LibroResumen.class);
        Page<LibroResumen> page = new PageImpl<>(List.of(resumen));
        when(libroRepository.busquedaMaestraAdminProyectada(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        Page<LibroResumen> result = libroService.busquedaGlobal("palabra", null, null, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("obtenerTodos - Should return all books")
    void obtenerTodos() {
        LibroResumen resumen = mock(LibroResumen.class);
        Page<LibroResumen> page = new PageImpl<>(List.of(resumen));
        when(libroRepository.busquedaMaestraAdminProyectada(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

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
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("buscarPorObraId - Should return results")
    void buscarPorObraId() {
        LibroResumen resumen = mock(LibroResumen.class);
        Page<LibroResumen> page = new PageImpl<>(List.of(resumen));
        when(libroRepository.buscarPorObraId(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<LibroResumen> result = libroService.buscarPorObraId(1L, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("crear - Should create and return DetalleResponse")
    void crearSuccess() {
        LibroRequest request = LibroRequest.builder()
                .isbn(JsonNullable.of("9780596520687"))
                .anioPublicacion(JsonNullable.of(2021))
                .idObra(JsonNullable.of(1L))
                .idFormato(JsonNullable.of(1L))
                .idEditorial(JsonNullable.of(1L))
                .idIdiomas(JsonNullable.of(Set.of(1L)))
                .build();
        Libro entity = Libro.builder().build();
        Libro saved = Libro.builder().id(1L).isbn("9780596520687").build();
        LibroDetalleResponse detail = mock(LibroDetalleResponse.class);

        when(libroRepository.existsByIsbn("9780596520687")).thenReturn(false);
        when(obraRepository.findById(1L)).thenReturn(Optional.of(Obra.builder().build()));
        when(formatoRepository.findById(1L)).thenReturn(Optional.of(Formato.builder().build()));
        when(editorialRepository.findById(1L)).thenReturn(Optional.of(Editorial.builder().build()));
        when(idiomaRepository.findById(1L)).thenReturn(Optional.of(Idioma.builder().build()));

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
        LibroRequest request = LibroRequest.builder()
                .isbn(JsonNullable.of("123"))
                .anioPublicacion(JsonNullable.of(2021))
                .idObra(JsonNullable.of(1L))
                .idFormato(JsonNullable.of(1L))
                .idEditorial(JsonNullable.of(1L))
                .idIdiomas(JsonNullable.of(Set.of(1L)))
                .build();

        when(libroRepository.existsByIsbn("123")).thenReturn(true);

        assertThatThrownBy(() -> libroService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un libro con el ISBN: 123");
    }

    @Test
    @DisplayName("actualizar - Should update and return DetalleResponse")
    void actualizar() {
        LibroRequest request = LibroRequest.builder()
                .isbn(JsonNullable.of("456"))
                .anioPublicacion(JsonNullable.of(2021))
                .idObra(JsonNullable.of(1L))
                .idFormato(JsonNullable.of(1L))
                .idEditorial(JsonNullable.of(1L))
                .idIdiomas(JsonNullable.of(Set.of(1L)))
                .version(0L)
                .build();
        Libro existing = Libro.builder().id(1L).isbn("123").version(0L).build();
        Libro saved = Libro.builder().id(1L).isbn("456").build();
        LibroDetalleResponse detail = mock(LibroDetalleResponse.class);

        when(libroRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(libroRepository.existsByIsbn("456")).thenReturn(false);
        when(obraRepository.findById(1L)).thenReturn(Optional.of(Obra.builder().build()));
        when(formatoRepository.findById(1L)).thenReturn(Optional.of(Formato.builder().build()));
        when(editorialRepository.findById(1L)).thenReturn(Optional.of(Editorial.builder().build()));
        when(idiomaRepository.findById(1L)).thenReturn(Optional.of(Idioma.builder().build()));

        when(libroRepository.save(existing)).thenReturn(saved);
        when(libroMapper.toDetalleResponse(saved)).thenReturn(detail);

        LibroDetalleResponse result = libroService.actualizar(1L, request);

        assertThat(result).isEqualTo(detail);
        verify(libroMapper).updateEntity(request, existing);
    }

    @Test
    @DisplayName("actualizar - Should update with same ISBN")
    void actualizarSameIsbn() {
        LibroRequest request = LibroRequest.builder()
                .isbn(JsonNullable.of("123"))
                .anioPublicacion(JsonNullable.of(2021))
                .idObra(JsonNullable.of(1L))
                .idFormato(JsonNullable.of(1L))
                .idEditorial(JsonNullable.of(1L))
                .idIdiomas(JsonNullable.of(Set.of(1L)))
                .version(0L)
                .build();
        Libro existing = Libro.builder().id(1L).isbn("123").version(0L).build();
        LibroDetalleResponse detail = mock(LibroDetalleResponse.class);

        when(libroRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(obraRepository.findById(1L)).thenReturn(Optional.of(Obra.builder().build()));
        when(formatoRepository.findById(1L)).thenReturn(Optional.of(Formato.builder().build()));
        when(editorialRepository.findById(1L)).thenReturn(Optional.of(Editorial.builder().build()));
        when(idiomaRepository.findById(1L)).thenReturn(Optional.of(Idioma.builder().build()));

        when(libroRepository.save(existing)).thenReturn(existing);
        when(libroMapper.toDetalleResponse(existing)).thenReturn(detail);

        libroService.actualizar(1L, request);

        verify(libroRepository, never()).existsByIsbn("123");
    }

    @Test
    @DisplayName("actualizar - Should throw when not found")
    void actualizarNotFound() {
        when(libroRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> libroService.actualizar(1L, LibroRequest.builder().build()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
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
}
