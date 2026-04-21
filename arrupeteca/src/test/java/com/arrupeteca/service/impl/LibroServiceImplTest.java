package com.arrupeteca.service.impl;

import java.util.List;
import java.util.Optional;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.LibroMapper;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import com.arrupeteca.persistence.projection.LibroResumen;
import com.arrupeteca.persistence.repository.EditorialRepository;
import com.arrupeteca.persistence.repository.FormatoRepository;
import com.arrupeteca.persistence.repository.IdiomaRepository;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.persistence.repository.ObraRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LibroServiceImplTest extends BaseServiceTest {

    @Mock
    private LibroRepository repository;
    @Mock
    private LibroMapper mapper;
    @Mock
    private ObraRepository obraRepository;
    @Mock
    private EditorialRepository editorialRepository;
    @Mock
    private FormatoRepository formatoRepository;
    @Mock
    private IdiomaRepository idiomaRepository;
    @InjectMocks
    private LibroServiceImpl service;

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
    @DisplayName("Should throw exception when active libro not found")
    void shouldThrowWhenActiveNotFound() {
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Libro activo con ID: 1");
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
    @DisplayName("Should return libro by id")
    void shouldReturnById() {
        LibroResumen resumen = mock(LibroResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorId(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when libro not found by id")
    void shouldThrowWhenNotFoundById() {
        when(repository.findProjectedById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Libro con ID: 1");
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
    @DisplayName("Should throw exception when obra not found in obtenerPorObra")
    void shouldThrowWhenObraNotFoundInObra() {
        when(obraRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> service.obtenerPorObra(1L, Pageable.unpaged()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Obra con ID: 1");
    }

    @Test
    @DisplayName("Should return busqueda maestra admin results")
    void shouldReturnBusquedaMaestraAdmin() {
        Page<LibroResumen> page = new PageImpl<>(List.of());
        when(repository.busquedaMaestraAdmin(any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(Pageable.class)))
                .thenReturn(page);

        assertThat(service.busquedaMaestraAdmin(false, 1L, 1L, 1L, "BACHI_TEC_SOFTWARE",
                "TERCER", "term", 2020, 2024, Pageable.unpaged()))
                .isEqualTo(page);

        verify(repository).busquedaMaestraAdmin(eq(false), eq(1L), eq(1L), eq(1L),
                eq(Grado.BACHI_TEC_SOFTWARE), eq(Ciclo.TERCER), eq("term"), eq(2020),
                eq(2024), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return busqueda maestra admin results with null params")
    void shouldReturnBusquedaMaestraAdminWithNulls() {
        Page<LibroResumen> page = new PageImpl<>(List.of());
        when(repository.busquedaMaestraAdmin(any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(Pageable.class)))
                .thenReturn(page);

        assertThat(service.busquedaMaestraAdmin(null, null, null, null, null, null, null, null, null,
                Pageable.unpaged()))
                .isEqualTo(page);

        verify(repository).busquedaMaestraAdmin(eq(null), eq(null), eq(null), eq(null),
                eq(null), eq(null), eq(null), eq(null), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("Should create libro with all relations")
    void shouldCreateWithRelations() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("1234567890");
        request.setIdObra(1L);
        request.setIdEditorial(1L);
        request.setIdFormato(1L);
        request.setIdIdiomas(java.util.Set.of(1L));

        when(repository.existsByIsbn("1234567890")).thenReturn(false);
        when(obraRepository.findById(1L))
                .thenReturn(Optional.of(mock(com.arrupeteca.persistence.entity.Obra.class)));
        when(editorialRepository.findById(1L))
                .thenReturn(Optional.of(mock(com.arrupeteca.persistence.entity.Editorial.class)));
        when(formatoRepository.findById(1L))
                .thenReturn(Optional.of(mock(com.arrupeteca.persistence.entity.Formato.class)));
        when(idiomaRepository.findAllById(any()))
                .thenReturn(List.of(mock(com.arrupeteca.persistence.entity.Idioma.class)));

        Libro entity = new Libro();
        entity.setId(1L);
        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);

        LibroResumen resumen = mock(LibroResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.crear(request)).isEqualTo(resumen);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Should throw exception when ISBN already exists on create")
    void shouldThrowWhenIsbnExistsOnCreate() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("1234567890");
        when(repository.existsByIsbn("1234567890")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El ISBN 1234567890 ya está registrado");
    }

    @Test
    @DisplayName("Should throw exception when Obra not found on create")
    void shouldThrowWhenObraNotFoundOnCreate() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("1234567890");
        request.setIdObra(1L);

        when(repository.existsByIsbn("1234567890")).thenReturn(false);
        when(obraRepository.findById(1L)).thenReturn(Optional.empty());
        when(mapper.toEntity(any())).thenReturn(new Libro());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("La Obra indicada no existe");
    }

    @Test
    @DisplayName("Should throw exception when Editorial not found on create")
    void shouldThrowWhenEditorialNotFoundOnCreate() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("1234567890");
        request.setIdEditorial(1L);

        when(repository.existsByIsbn("1234567890")).thenReturn(false);
        when(editorialRepository.findById(1L)).thenReturn(Optional.empty());
        when(mapper.toEntity(any())).thenReturn(new Libro());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("La Editorial indicada no existe");
    }

    @Test
    @DisplayName("Should throw exception when Formato not found on create")
    void shouldThrowWhenFormatoNotFoundOnCreate() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("1234567890");
        request.setIdFormato(1L);

        when(repository.existsByIsbn("1234567890")).thenReturn(false);
        when(formatoRepository.findById(1L)).thenReturn(Optional.empty());
        when(mapper.toEntity(any())).thenReturn(new Libro());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El Formato indicado no existe");
    }

    @Test
    @DisplayName("Should throw exception when Idiomas not found on create")
    void shouldThrowWhenIdiomasNotFoundOnCreate() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("1234567890");
        request.setIdIdiomas(java.util.Set.of(1L, 2L));

        when(repository.existsByIsbn("1234567890")).thenReturn(false);
        when(idiomaRepository.findAllById(any()))
                .thenReturn(List.of(mock(com.arrupeteca.persistence.entity.Idioma.class)));
        when(mapper.toEntity(any())).thenReturn(new Libro());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Uno o más idiomas indicados no existen");
    }

    @Test
    @DisplayName("Should update libro and change relations")
    void shouldUpdateWithRelations() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("0987654321");
        request.setIdObra(2L);

        Libro existing = new Libro();
        existing.setId(1L);
        existing.setIsbn("1234567890");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByIsbn("0987654321")).thenReturn(false);
        when(obraRepository.findById(2L))
                .thenReturn(Optional.of(mock(com.arrupeteca.persistence.entity.Obra.class)));

        LibroResumen resumen = mock(LibroResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.actualizar(1L, request)).isEqualTo(resumen);
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when libro not found on update")
    void shouldThrowWhenNotFoundOnUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(1L, new LibroRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Libro con ID: 1");
    }

    @Test
    @DisplayName("Should throw exception when ISBN already in use by another on update")
    void shouldThrowWhenIsbnInUseOnUpdate() {
        LibroRequest request = new LibroRequest();
        request.setIsbn("dup-isbn");

        Libro existing = new Libro();
        existing.setId(1L);
        existing.setIsbn("orig-isbn");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByIsbn("dup-isbn")).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("ya está en uso por otro libro");
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

    @Test
    @DisplayName("Should throw exception when libro not found on status change")
    void shouldThrowWhenNotFoundOnStatusChange() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> service.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Libro con ID: 1");
        verify(repository, never()).activar(any());
        verify(repository, never()).desactivar(any());
    }

}
